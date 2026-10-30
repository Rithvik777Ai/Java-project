package com.tn.smarthospital.controller;

import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.HospitalService;
import com.tn.smarthospital.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final HospitalService hospitalService;
    private final UserService userService;

    public DoctorController(DoctorService doctorService,
                            HospitalService hospitalService,
                            UserService userService) {
        this.doctorService = doctorService;
        this.hospitalService = hospitalService;
        this.userService = userService;
    }

    @GetMapping
    public String listDoctors(@RequestParam(value = "district", required = false) String district,
                              @RequestParam(value = "specialty", required = false) String specialty,
                              @RequestParam(value = "hospitalId", required = false) Long hospitalId,
                              Model model) {
        List<Doctor> doctors = doctorService.searchDoctors(district, specialty, hospitalId);

        model.addAttribute("doctors", doctors);
        model.addAttribute("resultCount", doctors.size());
        model.addAttribute("selectedDistrict", district);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("selectedHospitalId", hospitalId);
        model.addAttribute("districts", hospitalService.getAllDistricts());
        model.addAttribute("specialties", doctorService.getAllSpecialties());
        model.addAttribute("hospitals", hospitalService.getAllActiveHospitals());

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "doctors/list";
    }

    @GetMapping("/{id}")
    public String doctorDetail(@PathVariable("id") Long id,
                               @RequestParam(value = "date", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate selectedDate,
                               Model model) {
        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + id));

        LocalDate date = (selectedDate != null) ? selectedDate : LocalDate.now();
        List<AvailabilitySlot> availableSlots = doctorService.getAvailableSlotsForDoctorAndDate(id, date);

        List<AvailabilitySlot> morningSlots = new ArrayList<>();
        List<AvailabilitySlot> afternoonSlots = new ArrayList<>();
        LocalTime noon = LocalTime.of(12, 0);

        for (AvailabilitySlot slot : availableSlots) {
            if (slot.getStartTime().isBefore(noon)) {
                morningSlots.add(slot);
            } else {
                afternoonSlots.add(slot);
            }
        }

        model.addAttribute("doctor", doctor);
        model.addAttribute("selectedDate", date);
        model.addAttribute("morningSlots", morningSlots);
        model.addAttribute("afternoonSlots", afternoonSlots);
        model.addAttribute("totalAvailableSlots", availableSlots.size());

        List<LocalDate> nextSevenDays = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            nextSevenDays.add(LocalDate.now().plusDays(i));
        }
        model.addAttribute("availableDates", nextSevenDays);

        Optional<User> currentUser = userService.getCurrentUser();
        currentUser.ifPresent(user -> model.addAttribute("currentUser", user));

        return "doctors/detail";
    }
}

