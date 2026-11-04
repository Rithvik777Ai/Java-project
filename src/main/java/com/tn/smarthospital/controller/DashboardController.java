package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.SlotForm;
import com.tn.smarthospital.model.*;
import com.tn.smarthospital.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final HospitalService hospitalService;
    private final NotificationService notificationService;
    private final TextBeeSmsService textBeeSmsService;

    public DashboardController(UserService userService,
                               AppointmentService appointmentService,
                               DoctorService doctorService,
                               HospitalService hospitalService,
                               NotificationService notificationService,
                               TextBeeSmsService textBeeSmsService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.hospitalService = hospitalService;
        this.notificationService = notificationService;
        this.textBeeSmsService = textBeeSmsService;
    }

    /**
     * Master dashboard router.
     * Evaluates the authenticated user's role and redirects to the appropriate role-based dashboard.
     * If anonymous, defaults to the Patient Demo Login.
     */
    @GetMapping("/dashboard")
    public String dashboardRouter() {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            if (user.getRole() == Role.ROLE_ADMIN) {
                return "redirect:/dashboard/admin";
            } else if (user.getRole() == Role.ROLE_DOCTOR) {
                return "redirect:/dashboard/doctor";
            } else {
                return "redirect:/dashboard/patient";
            }
        }
        return "redirect:/demo-login?role=PATIENT";
    }

    /**
     * Patient Dashboard.
     */
    @GetMapping("/dashboard/patient")
    public String patientDashboard(Model model) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/demo-login?role=PATIENT";
        }
        User user = currentUserOpt.get();

        List<Appointment> allAppointments = appointmentService.getPatientAppointments(user.getId());
        List<Appointment> upcomingAppointments = new ArrayList<>();
        List<Appointment> completedAppointments = new ArrayList<>();
        List<Appointment> cancelledAppointments = new ArrayList<>();
        List<Appointment> pastAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Appointment appt : allAppointments) {
            if (appt.getStatus() == AppointmentStatus.CONFIRMED && !appt.getDate().isBefore(today)) {
                upcomingAppointments.add(appt);
            } else if (appt.getStatus() == AppointmentStatus.COMPLETED) {
                completedAppointments.add(appt);
                pastAppointments.add(appt);
            } else if (appt.getStatus() == AppointmentStatus.CANCELLED) {
                cancelledAppointments.add(appt);
                pastAppointments.add(appt);
            } else {
                pastAppointments.add(appt);
            }
        }

        List<Notification> notifications = notificationService.getUserNotifications(user.getId());
        long unreadCount = notificationService.getUnreadCount(user.getId());

        model.addAttribute("currentUser", user);
        model.addAttribute("allAppointments", allAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);
        model.addAttribute("pastAppointments", pastAppointments);
        model.addAttribute("totalAppointments", allAppointments.size());
        model.addAttribute("upcomingCount", upcomingAppointments.size());
        model.addAttribute("completedCount", completedAppointments.size());
        model.addAttribute("cancelledCount", cancelledAppointments.size());
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);

        return "dashboard/patient-dashboard";
    }

    /**
     * Doctor Dashboard.
     */
    @GetMapping("/dashboard/doctor")
    public String doctorDashboard(@RequestParam(value = "date", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
                                  Model model) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/demo-login?role=DOCTOR";
        }
        User user = currentUserOpt.get();

        Optional<Doctor> doctorOpt = doctorService.getDoctorByUser(user);
        Doctor doctor;
        if (doctorOpt.isPresent()) {
            doctor = doctorOpt.get();
        } else {
            List<Doctor> doctors = doctorService.getAllActiveDoctors();
            if (doctors.isEmpty()) {
                throw new IllegalStateException("No doctor profile available in the system.");
            }
            doctor = doctors.get(0);
        }

        List<Appointment> allDoctorAppointments = appointmentService.getDoctorAppointments(doctor.getId());
        List<Appointment> todayAppointments = new ArrayList<>();
        List<Appointment> upcomingAppointments = new ArrayList<>();
        List<Appointment> completedAppointments = new ArrayList<>();
        List<Appointment> cancelledAppointments = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (Appointment appt : allDoctorAppointments) {
            if (appt.getDate().equals(today)) {
                todayAppointments.add(appt);
            }
            if (appt.getStatus() == AppointmentStatus.CONFIRMED && !appt.getDate().isBefore(today)) {
                upcomingAppointments.add(appt);
            } else if (appt.getStatus() == AppointmentStatus.COMPLETED) {
                completedAppointments.add(appt);
            } else if (appt.getStatus() == AppointmentStatus.CANCELLED) {
                cancelledAppointments.add(appt);
            }
        }

        LocalDate slotFilterDate = (selectedDate != null) ? selectedDate : today;
        List<AvailabilitySlot> activeSlots = doctorService.getAllSlotsForDoctorAndDate(doctor.getId(), slotFilterDate);

        model.addAttribute("currentUser", user);
        model.addAttribute("doctor", doctor);
        model.addAttribute("appointments", allDoctorAppointments);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("cancelledAppointments", cancelledAppointments);
        model.addAttribute("totalAppointments", allDoctorAppointments.size());
        model.addAttribute("upcomingCount", upcomingAppointments.size());
        model.addAttribute("completedCount", completedAppointments.size());
        model.addAttribute("cancelledCount", cancelledAppointments.size());
        model.addAttribute("activeSlots", activeSlots);
        model.addAttribute("slotForm", new SlotForm());
        model.addAttribute("selectedDate", slotFilterDate);

        return "dashboard/doctor-dashboard";
    }

    /**
     * Administrator Dashboard.
     */
    @GetMapping("/dashboard/admin")
    public String adminDashboard(@RequestParam(value = "date", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 @RequestParam(value = "hospitalId", required = false) Long hospitalId,
                                 @RequestParam(value = "doctorId", required = false) Long doctorId,
                                 @RequestParam(value = "status", required = false) AppointmentStatus status,
                                 Model model) {
        Optional<User> currentUserOpt = userService.getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/demo-login?role=ADMIN";
        }

        List<Appointment> filteredAppointments = appointmentService.getAllAppointmentsAdmin(date, hospitalId, doctorId, status);
        List<Hospital> allHospitals = hospitalService.getAllHospitals();
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        List<User> allUsers = userService.getAllUsers();

        long activeHospitals = allHospitals.stream().filter(Hospital::isActive).count();

        model.addAttribute("currentUser", currentUserOpt.get());
        model.addAttribute("appointments", filteredAppointments);
        model.addAttribute("hospitals", allHospitals);
        model.addAttribute("activeHospitalsCount", activeHospitals);
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("users", allUsers);
        model.addAttribute("totalHospitals", hospitalService.getTotalHospitalsCount());
        model.addAttribute("totalDoctors", doctorService.getTotalDoctorsCount());
        model.addAttribute("totalUsers", userService.getTotalUsersCount());
        model.addAttribute("totalAppointments", appointmentService.getSystemStatistics().get("totalBookings"));
        model.addAttribute("stats", appointmentService.getSystemStatistics());
        model.addAttribute("smsService", textBeeSmsService);
        model.addAttribute("districts", hospitalService.getAllDistricts());
        model.addAttribute("specialties", doctorService.getAllSpecialties());
        model.addAttribute("selectedDate", date);
        model.addAttribute("selectedHospitalId", hospitalId);
        model.addAttribute("selectedDoctorId", doctorId);
        model.addAttribute("selectedStatus", status);

        return "dashboard/admin-dashboard";
    }

    /**
     * Appointments route: redirects to the appropriate dashboard appointment view.
     */
    @GetMapping("/appointments")
    public String appointmentsRouter() {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            if (user.getRole() == Role.ROLE_ADMIN) {
                return "redirect:/dashboard/admin#appointments";
            } else if (user.getRole() == Role.ROLE_DOCTOR) {
                return "redirect:/dashboard/doctor#appointments";
            } else {
                return "redirect:/dashboard/patient#appointments";
            }
        }
        return "redirect:/demo-login?role=PATIENT";
    }

    /**
     * Appointment booking shortcut.
     */
    @GetMapping("/appointments/book")
    public String appointmentsBook(@RequestParam(value = "slotId", required = false) Long slotId) {
        if (slotId != null) {
            return "redirect:/book?slotId=" + slotId;
        }
        return "redirect:/doctors";
    }

    /**
     * Profile route.
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        Optional<User> currentUser = userService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser.get());
        return "profile";
    }
}

