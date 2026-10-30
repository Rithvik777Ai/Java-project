package com.tn.smarthospital.controller;

import com.tn.smarthospital.dto.HospitalSearchForm;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.HospitalService;
import com.tn.smarthospital.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final UserService userService;

    public HospitalController(HospitalService hospitalService,
                              DoctorService doctorService,
                              UserService userService) {
        this.hospitalService = hospitalService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    @GetMapping
    public String listHospitals(@ModelAttribute("searchForm") HospitalSearchForm searchForm, Model model) {
        List<Hospital> hospitals = hospitalService.searchHospitals(searchForm);

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("resultCount", hospitals.size());
        model.addAttribute("districts", hospitalService.getAllDistricts());
        model.addAttribute("specialties", doctorService.getAllSpecialties());
        model.addAttribute("hospitalTypes", hospitalService.getAllHospitalTypes());

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "hospitals/list";
    }

    @GetMapping("/{id}")
    public String hospitalDetail(@PathVariable("id") Long id, Model model) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + id));

        List<Doctor> doctors = doctorService.getDoctorsByHospital(id);

        model.addAttribute("hospital", hospital);
        model.addAttribute("doctors", doctors);

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "hospitals/detail";
    }
}

