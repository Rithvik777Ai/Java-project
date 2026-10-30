package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.SlotForm;
import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorPortalController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    public DoctorPortalController(DoctorService doctorService,
                                  AppointmentService appointmentService,
                                  UserService userService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "date", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
                            Model model) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        User user = currentUserOpt.get();

        Optional<Doctor> doctorOpt = doctorService.getDoctorByUser(user);
        Doctor doctor;
        if (doctorOpt.isPresent()) {
            doctor = doctorOpt.get();
        } else {
            // Fallback for Admin or doctors with no direct user link
            List<Doctor> doctors = doctorService.getAllActiveDoctors();
            if (doctors.isEmpty()) {
                throw new IllegalStateException("No doctor profile available.");
            }
            doctor = doctors.get(0);
        }

        LocalDate date = (selectedDate != null) ? selectedDate : LocalDate.now();
        List<Appointment> allAppointments = appointmentService.getDoctorAppointments(doctor.getId());
        List<AvailabilitySlot> todaySlots = doctorService.getAllSlotsForDoctorAndDate(doctor.getId(), date);

        SlotForm slotForm = new SlotForm();
        slotForm.setDoctorId(doctor.getId());
        slotForm.setDate(date);

        model.addAttribute("currentUser", user);
        model.addAttribute("doctor", doctor);
        model.addAttribute("selectedDate", date);
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("todaySlots", todaySlots);
        model.addAttribute("slotForm", slotForm);

        return "doctor/dashboard";
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        try {
            appointmentService.markAppointmentCompleted(id, currentUserOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "Consultation marked as Completed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/doctor/dashboard";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        try {
            appointmentService.cancelAppointment(id, reason, currentUserOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled and slot reopened.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/doctor/dashboard";
    }

    @PostMapping("/slots/add")
    public String addSlot(@Valid @ModelAttribute("slotForm") SlotForm slotForm,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid slot data. Please verify date, start time, and end time.");
            return "redirect:/doctor/dashboard?date=" + slotForm.getDate();
        }

        try {
            doctorService.createSlot(slotForm);
            redirectAttributes.addFlashAttribute("successMessage", "New availability slot added successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/doctor/dashboard?date=" + slotForm.getDate();
    }

    @PostMapping("/slots/{id}/delete")
    public String deleteSlot(@PathVariable("id") Long slotId,
                             @RequestParam(value = "date", required = false) String date,
                             RedirectAttributes redirectAttributes) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        Doctor doctor = doctorService.getDoctorByUser(currentUserOpt.get()).orElse(null);
        Long doctorId = (doctor != null) ? doctor.getId() : null;

        try {
            doctorService.deleteSlot(slotId, doctorId);
            redirectAttributes.addFlashAttribute("successMessage", "Availability slot removed.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/doctor/dashboard" + (date != null ? "?date=" + date : "");
    }

    @PostMapping("/slots/generate-week")
    public String generateWeekSlots(@RequestParam("doctorId") Long doctorId,
                                    RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        int generated = doctorService.generateStandardSlotsForDoctor(doctor, LocalDate.now(), 7);
        redirectAttributes.addFlashAttribute("successMessage", "Generated " + generated + " new availability slots for the upcoming 7 days.");
        return "redirect:/doctor/dashboard";
    }
}

