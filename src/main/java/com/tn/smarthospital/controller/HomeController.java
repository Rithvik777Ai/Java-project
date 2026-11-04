package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.HospitalSearchForm;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.model.Role;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.HospitalService;
import com.tn.smarthospital.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final UserService userService;

    public HomeController(HospitalService hospitalService,
                          DoctorService doctorService,
                          AppointmentService appointmentService,
                          UserService userService) {
        this.hospitalService = hospitalService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchForm", new HospitalSearchForm());
        model.addAttribute("districts", hospitalService.getAllDistricts());
        model.addAttribute("specialties", doctorService.getAllSpecialties());
        model.addAttribute("hospitalTypes", hospitalService.getAllHospitalTypes());

        List<Hospital> allHospitals = hospitalService.getAllActiveHospitals();
        model.addAttribute("featuredHospitals", allHospitals.subList(0, Math.min(6, allHospitals.size())));

        model.addAttribute("totalHospitals", hospitalService.getTotalHospitalsCount());
        model.addAttribute("totalDoctors", doctorService.getTotalDoctorsCount());
        model.addAttribute("totalDistricts", hospitalService.getAllDistricts().size());
        model.addAttribute("stats", appointmentService.getSystemStatistics());

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));
        return "about";
    }

    @GetMapping("/dashboard-redirect")
    public String dashboardRedirect() {
        return "redirect:/dashboard";
    }
}

