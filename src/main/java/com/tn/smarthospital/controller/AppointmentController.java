package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.BookingRequest;
import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.SlotStatus;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService,
                                 DoctorService doctorService,
                                 UserService userService) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping("/book")
    public String showBookingForm(@RequestParam("slotId") Long slotId, Model model, RedirectAttributes redirectAttributes) {
        Optional<AvailabilitySlot> slotOpt = doctorService.getSlotById(slotId);
        if (slotOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The requested appointment slot does not exist.");
            return "redirect:/doctors";
        }

        AvailabilitySlot slot = slotOpt.get();
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            redirectAttributes.addFlashAttribute("errorMessage", "This slot is no longer available. Please select another slot.");
            return "redirect:/doctors/" + slot.getDoctor().getId();
        }

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setSlotId(slot.getId());

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            bookingRequest.setPatientName(user.getName());
            bookingRequest.setPatientEmail(user.getEmail());
            bookingRequest.setPatientPhone(user.getPhone());
            model.addAttribute("currentUser", user);
        }

        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("slot", slot);
        model.addAttribute("doctor", slot.getDoctor());
        model.addAttribute("hospital", slot.getDoctor().getHospital());

        return "appointments/book";
    }

    @PostMapping("/book")
    public String processBooking(@Valid @ModelAttribute("bookingRequest") BookingRequest bookingRequest,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<AvailabilitySlot> slotOpt = doctorService.getSlotById(bookingRequest.getSlotId());
        if (slotOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected slot is not valid.");
            return "redirect:/doctors";
        }
        AvailabilitySlot slot = slotOpt.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("slot", slot);
            model.addAttribute("doctor", slot.getDoctor());
            model.addAttribute("hospital", slot.getDoctor().getHospital());
            Optional<User> currentUser = userService.getCurrentUser();
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
            return "appointments/book";
        }

        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please sign in or use demo login to complete the booking.");
            return "redirect:/login";
        }

        try {
            Appointment appointment = appointmentService.bookAppointment(bookingRequest, currentUser.get());
            redirectAttributes.addFlashAttribute("successMessage", "Your demo appointment has been saved successfully. Reference: " + appointment.getBookingReference());
            return "redirect:/appointments/confirmation/" + appointment.getBookingReference();
        } catch (IllegalStateException | IllegalArgumentException ex) {
            model.addAttribute("slot", slot);
            model.addAttribute("doctor", slot.getDoctor());
            model.addAttribute("hospital", slot.getDoctor().getHospital());
            model.addAttribute("bookingError", ex.getMessage());
            currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
            return "appointments/book";
        }
    }

    @GetMapping("/appointments/confirmation/{reference}")
    public String confirmationSlip(@PathVariable("reference") String reference, Model model, RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentByReference(reference);
        if (appointmentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment record not found.");
            return "redirect:/patient/dashboard";
        }

        model.addAttribute("appointment", appointmentOpt.get());
        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "appointments/confirmation";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id,
                                    @RequestParam(value = "reason", required = false) String reason,
                                    RedirectAttributes redirectAttributes) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return "redirect:/login";
        }

        try {
            appointmentService.cancelAppointment(id, reason, currentUser.get());
            redirectAttributes.addFlashAttribute("successMessage", "Demo appointment cancelled successfully. The time slot is now available again.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/patient/dashboard";
    }
}

