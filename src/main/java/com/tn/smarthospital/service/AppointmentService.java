package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.BookingRequest;
import com.tn.smarthospital.dto.SmsResult;
import com.tn.smarthospital.model.*;
import com.tn.smarthospital.repository.AppointmentRepository;
import com.tn.smarthospital.repository.AvailabilitySlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final NotificationService notificationService;
    private final TextBeeSmsService textBeeSmsService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              AvailabilitySlotRepository slotRepository,
                              NotificationService notificationService,
                              TextBeeSmsService textBeeSmsService) {
        this.appointmentRepository = appointmentRepository;
        this.slotRepository = slotRepository;
        this.notificationService = notificationService;
        this.textBeeSmsService = textBeeSmsService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized Appointment bookAppointment(BookingRequest request, User patientUser) {
        AvailabilitySlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new IllegalArgumentException("Selected slot does not exist."));

        // 1. Verify slot is still AVAILABLE
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Sorry, this time slot has just been booked by another patient. Please select another slot.");
        }

        // 2. Prevent duplicate booking for the same patient and slot
        if (appointmentRepository.existsByPatientIdAndSlotIdAndStatusNot(
                patientUser.getId(), slot.getId(), AppointmentStatus.CANCELLED)) {
            throw new IllegalStateException("You already have an active appointment booked for this slot.");
        }

        // 3. Mark slot as BOOKED
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        // 4. Generate unique readable reference: e.g. TNSH-20261031-4829
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String reference;
        do {
            int randomCode = ThreadLocalRandom.current().nextInt(1000, 9999);
            reference = "TNSH-" + datePrefix + "-" + randomCode;
        } while (appointmentRepository.findByBookingReference(reference).isPresent());

        // 5. Create Appointment with CuraTransit Travel Assistance Options
        Appointment appointment = new Appointment(
                reference,
                patientUser,
                request.getPatientName().trim(),
                request.getPatientPhone().trim(),
                request.getPatientEmail() != null ? request.getPatientEmail().trim() : patientUser.getEmail(),
                slot.getDoctor(),
                slot.getDoctor().getHospital(),
                slot,
                slot.getDate(),
                slot.getStartTime(),
                request.getReason() != null ? request.getReason().trim() : "General Consultation"
        );

        appointment.setNeedsWheelchair(request.isNeedsWheelchair());
        appointment.setNeedsLanguageSupport(request.isNeedsLanguageSupport());
        appointment.setPreferredLanguage(request.getPreferredLanguage());
        appointment.setOutstationPatient(request.isOutstationPatient());
        appointment.setTravelOrigin(request.getTravelOrigin());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // 6. Send demo in-app notification
        notificationService.createNotification(
                patientUser,
                "Demo Appointment Confirmed! Ref: " + savedAppointment.getBookingReference() +
                        " with " + slot.getDoctor().getName() + " on " + savedAppointment.getFormattedDate() + " at " + savedAppointment.getFormattedTime(),
                "BOOKING_CONFIRMED"
        );

        // 7. Dispatch SMS Notification via TextBee (safe, avoids duplicate sends)
        if (!savedAppointment.isSmsNotificationSent()) {
            try {
                SmsResult smsResult = textBeeSmsService.sendBookingNotification(savedAppointment);
                savedAppointment.setSmsNotificationStatus(smsResult.getStatus());
                savedAppointment.setSmsNotificationDetails(smsResult.getMessage());
                if (smsResult.isSuccess()) {
                    savedAppointment.setSmsNotificationSent(true);
                }
            } catch (Exception ex) {
                savedAppointment.setSmsNotificationStatus("FAILED");
                savedAppointment.setSmsNotificationDetails("SMS delivery failed: " + ex.getMessage());
            }
            savedAppointment = appointmentRepository.save(savedAppointment);
        }

        return savedAppointment;
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String reason, User actor) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment is already cancelled.");
        }

        // Check permission: Patient owner, the Doctor, or an Admin
        boolean isPatient = actor.getRole() == Role.ROLE_PATIENT && appointment.getPatient().getId().equals(actor.getId());
        boolean isDoctor = actor.getRole() == Role.ROLE_DOCTOR && appointment.getDoctor().getUser() != null &&
                           appointment.getDoctor().getUser().getId().equals(actor.getId());
        boolean isAdmin = actor.getRole() == Role.ROLE_ADMIN;

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new SecurityException("You do not have permission to cancel this appointment.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason != null && !reason.trim().isEmpty() ? reason.trim() : "Cancelled by user");
        appointmentRepository.save(appointment);

        // Release slot back to AVAILABLE if date is today or future
        AvailabilitySlot slot = appointment.getSlot();
        if (slot != null && !slot.getDate().isBefore(LocalDate.now())) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }

        // Send in-app notification
        notificationService.createNotification(
                appointment.getPatient(),
                "Your appointment (Ref: " + appointment.getBookingReference() + ") with " +
                        appointment.getDoctor().getName() + " on " + appointment.getFormattedDate() + " has been cancelled.",
                "BOOKING_CANCELLED"
        );

        // Send Cancellation SMS via TextBee (safe)
        try {
            SmsResult smsResult = textBeeSmsService.sendCancellationNotification(appointment);
            if (smsResult != null) {
                appointment.setSmsNotificationStatus(smsResult.getStatus());
                appointment.setSmsNotificationDetails(smsResult.getMessage());
                appointmentRepository.save(appointment);
            }
        } catch (Exception ex) {
            // Logged inside TextBeeSmsService; appointment state is preserved
        }
    }

    @Transactional
    public void markAppointmentCompleted(Long appointmentId, User doctorUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        // Check if actor is the doctor or admin
        boolean isDoctor = doctorUser.getRole() == Role.ROLE_DOCTOR && appointment.getDoctor().getUser() != null &&
                appointment.getDoctor().getUser().getId().equals(doctorUser.getId());
        boolean isAdmin = doctorUser.getRole() == Role.ROLE_ADMIN;

        if (!isDoctor && !isAdmin) {
            throw new SecurityException("Unauthorized to complete this appointment.");
        }

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed appointments can be marked as completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        notificationService.createNotification(
                appointment.getPatient(),
                "Your demo consultation (Ref: " + appointment.getBookingReference() + ") with " +
                        appointment.getDoctor().getName() + " has been marked as Completed.",
                "APPOINTMENT_COMPLETED"
        );

        // Send Status Update SMS via TextBee (safe)
        try {
            textBeeSmsService.sendStatusUpdateNotification(appointment, "COMPLETED");
        } catch (Exception ex) {
            // Logged inside TextBeeSmsService; appointment state is preserved
        }
    }

    public Optional<Appointment> getAppointmentByReference(String reference) {
        return appointmentRepository.findByBookingReference(reference);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByDateDescTimeDesc(patientId);
    }

    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByDateDescTimeDesc(doctorId);
    }

    public List<Appointment> getAllAppointmentsAdmin(LocalDate date, Long hospitalId, Long doctorId, AppointmentStatus status) {
        return appointmentRepository.searchAppointmentsAdmin(date, hospitalId, doctorId, status);
    }

    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBookings", appointmentRepository.count());
        stats.put("confirmedBookings", appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED));
        stats.put("completedBookings", appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        stats.put("cancelledBookings", appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));
        return stats;
    }
}

