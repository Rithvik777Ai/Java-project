package com.tn.smarthospital.controller.api;

import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.service.DoctorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorApiController {

    private final DoctorService doctorService;

    public DoctorApiController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String specialty) {

        List<Doctor> doctors;
        if (hospitalId != null) {
            doctors = doctorService.getDoctorsByHospital(hospitalId);
        } else if (district != null || specialty != null) {
            doctors = doctorService.searchDoctors(district, specialty, null);
        } else {
            doctors = doctorService.getAllActiveDoctors();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", doctors.size());
        response.put("data", doctors);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", doctor);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<Map<String, Object>> getSlots(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        LocalDate targetDate = date != null ? date : LocalDate.now().plusDays(1);
        List<AvailabilitySlot> slots = doctorService.getAvailableSlotsForDoctorAndDate(id, targetDate);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("doctorId", doctor.getId());
        response.put("doctorName", doctor.getName());
        response.put("date", targetDate);
        response.put("availableSlotsCount", slots.size());
        response.put("data", slots);

        return ResponseEntity.ok(response);
    }
}

