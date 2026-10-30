package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.DoctorForm;
import com.tn.smarthospital.dto.SlotForm;
import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.model.SlotStatus;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.repository.AvailabilitySlotRepository;
import com.tn.smarthospital.repository.DoctorRepository;
import com.tn.smarthospital.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final AvailabilitySlotRepository slotRepository;

    public DoctorService(DoctorRepository doctorRepository,
                         HospitalRepository hospitalRepository,
                         AvailabilitySlotRepository slotRepository) {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
        this.slotRepository = slotRepository;
    }

    public List<Doctor> getAllActiveDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> getDoctorByUser(User user) {
        return doctorRepository.findByUser(user);
    }

    public List<Doctor> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdAndActiveTrue(hospitalId);
    }

    public List<Doctor> searchDoctors(String district, String specialty, Long hospitalId) {
        String dist = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
        String spec = (specialty != null && !specialty.trim().isEmpty()) ? specialty.trim() : null;
        return doctorRepository.searchDoctors(dist, spec, hospitalId);
    }

    public List<String> getAllSpecialties() {
        return doctorRepository.findDistinctSpecialties();
    }

    public List<AvailabilitySlot> getAvailableSlotsForDoctorAndDate(Long doctorId, LocalDate date) {
        return slotRepository.findByDoctorIdAndDateAndStatusOrderByStartTimeAsc(doctorId, date, SlotStatus.AVAILABLE);
    }

    public List<AvailabilitySlot> getAllSlotsForDoctorAndDate(Long doctorId, LocalDate date) {
        return slotRepository.findByDoctorIdAndDateOrderByStartTimeAsc(doctorId, date);
    }

    public List<AvailabilitySlot> getUpcomingSlotsForDoctor(Long doctorId) {
        return slotRepository.findByDoctorIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(doctorId, LocalDate.now());
    }

    public List<LocalDate> getAvailableDatesForDoctor(Long doctorId) {
        return slotRepository.findAvailableDatesForDoctor(doctorId, LocalDate.now());
    }

    public Optional<AvailabilitySlot> getSlotById(Long slotId) {
        return slotRepository.findById(slotId);
    }

    @Transactional
    public Doctor saveDoctor(DoctorForm form) {
        Hospital hospital = hospitalRepository.findById(form.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + form.getHospitalId()));

        Doctor doctor;
        if (form.getId() != null) {
            doctor = doctorRepository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + form.getId()));
        } else {
            doctor = new Doctor();
            doctor.setDemo(true);
        }

        doctor.setName(form.getName().trim());
        doctor.setSpecialty(form.getSpecialty().trim());
        doctor.setQualification(form.getQualification() != null ? form.getQualification().trim() : "MBBS");
        doctor.setExperienceYears(form.getExperienceYears() != null ? form.getExperienceYears() : 5);
        doctor.setHospital(hospital);
        doctor.setLanguages(form.getLanguages() != null ? form.getLanguages().trim() : "Tamil, English");
        doctor.setConsultationType(form.getConsultationType() != null ? form.getConsultationType().trim() : "In-Person Consultation");
        doctor.setConsultationFee(form.getConsultationFee() != null ? form.getConsultationFee() : 500.0);
        doctor.setRoomNumber(form.getRoomNumber() != null ? form.getRoomNumber().trim() : "OPD 101");
        doctor.setActive(form.isActive());

        return doctorRepository.save(doctor);
    }

    @Transactional
    public void toggleDoctorStatus(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));
        doctor.setActive(!doctor.isActive());
        doctorRepository.save(doctor);
    }

    @Transactional
    public AvailabilitySlot createSlot(SlotForm form) {
        Doctor doctor = doctorRepository.findById(form.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + form.getDoctorId()));

        if (form.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create appointment slots in the past.");
        }
        if (!form.getEndTime().isAfter(form.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        // Check if slot already exists for doctor at this date and start time
        Optional<AvailabilitySlot> existing = slotRepository.findByDoctorIdAndDateAndStartTime(
                doctor.getId(), form.getDate(), form.getStartTime());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A slot already exists for this doctor on " + form.getDate() + " at " + form.getStartTime());
        }

        AvailabilitySlot slot = new AvailabilitySlot(doctor, form.getDate(), form.getStartTime(), form.getEndTime());
        return slotRepository.save(slot);
    }

    @Transactional
    public void deleteSlot(Long slotId, Long doctorId) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));

        if (doctorId != null && !slot.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Unauthorized to modify this slot.");
        }

        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new IllegalStateException("Cannot delete an already booked slot. The appointment must be cancelled first.");
        }

        slotRepository.delete(slot);
    }

    @Transactional
    public int generateStandardSlotsForDoctor(Doctor doctor, LocalDate startDate, int daysCount) {
        int createdCount = 0;
        LocalTime[] slotTimes = new LocalTime[]{
                LocalTime.of(9, 0), LocalTime.of(9, 30),
                LocalTime.of(10, 0), LocalTime.of(10, 30),
                LocalTime.of(11, 0), LocalTime.of(11, 30),
                LocalTime.of(16, 0), LocalTime.of(16, 30),
                LocalTime.of(17, 0), LocalTime.of(17, 30)
        };

        for (int d = 0; d < daysCount; d++) {
            LocalDate date = startDate.plusDays(d);
            for (LocalTime start : slotTimes) {
                LocalTime end = start.plusMinutes(30);
                if (slotRepository.findByDoctorIdAndDateAndStartTime(doctor.getId(), date, start).isEmpty()) {
                    AvailabilitySlot slot = new AvailabilitySlot(doctor, date, start, end);
                    slotRepository.save(slot);
                    createdCount++;
                }
            }
        }
        return createdCount;
    }

    public long getTotalDoctorsCount() {
        return doctorRepository.count();
    }
}

