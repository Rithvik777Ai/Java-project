package com.tn.smarthospital.controller;

import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.AppointmentStatus;
import com.tn.smarthospital.model.Notification;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.NotificationService;
import com.tn.smarthospital.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final AppointmentService appointmentService;
    private final NotificationService notificationService;
    private final UserService userService;

    public PatientController(AppointmentService appointmentService,
                             NotificationService notificationService,
                             UserService userService) {
        this.appointmentService = appointmentService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "redirect:/dashboard/patient";
    }

    @PostMapping("/notifications/mark-read")
    public String markNotificationsRead(RedirectAttributes redirectAttributes) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        currentUserOpt.ifPresent(user -> notificationService.markAllAsRead(user.getId()));
        return "redirect:/patient/dashboard";
    }
}

