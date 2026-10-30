package com.tn.smarthospital.controller.api;

import com.tn.smarthospital.dto.HospitalSearchForm;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.service.DoctorService;
import com.tn.smarthospital.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalApiController {

    private final HospitalService hospitalService;
    private final DoctorService doctorService;

    public HospitalApiController(HospitalService hospitalService, DoctorService doctorService) {
        this.hospitalService = hospitalService;
        this.doctorService = doctorService;
    }

    /**
     * Search and filter hospitals with CuraTransit criteria (district, specialty, emergency, ICU, query, sort).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchHospitals(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String hospitalType,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean hasEmergency,
            @RequestParam(required = false) Boolean hasIcu,
            @RequestParam(required = false, defaultValue = "name") String sortBy) {

        HospitalSearchForm form = new HospitalSearchForm();
        form.setDistrict(district);
        form.setCity(city);
        form.setSpecialty(specialty);
        form.setHospitalType(hospitalType);
        form.setQuery(query);
        form.setHasEmergency(hasEmergency);
        form.setHasIcu(hasIcu);
        form.setSortBy(sortBy);

        List<Hospital> hospitals = hospitalService.searchHospitals(form);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", hospitals.size());
        response.put("data", hospitals);

        return ResponseEntity.ok(response);
    }

    /**
     * Get single hospital details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHospital(@PathVariable Long id) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with ID: " + id));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", hospital);
        response.put("doctorsCount", hospital.getDoctors().size());

        return ResponseEntity.ok(response);
    }

    /**
     * Side-by-side comparison endpoint for 2 to 4 hospitals.
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareHospitals(@RequestParam List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Please provide hospital IDs to compare (e.g., ?ids=1,2)");
        }
        if (ids.size() > 4) {
            throw new IllegalArgumentException("Maximum 4 hospitals can be compared simultaneously");
        }

        List<Hospital> hospitals = hospitalService.getHospitalsForComparison(ids);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("comparedCount", hospitals.size());
        response.put("data", hospitals);

        return ResponseEntity.ok(response);
    }

    /**
     * Dedicated travel assistance and transit details for outstation patients.
     */
    @GetMapping("/{id}/transit")
    public ResponseEntity<Map<String, Object>> getTransitAssistance(@PathVariable Long id) {
        Hospital hospital = hospitalService.getHospitalById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found with ID: " + id));

        Map<String, Object> transitInfo = new LinkedHashMap<>();
        transitInfo.put("hospitalId", hospital.getId());
        transitInfo.put("hospitalName", hospital.getName());
        transitInfo.put("district", hospital.getDistrict());
        transitInfo.put("nearestRailwayStation", hospital.getNearestRailwayStation());
        transitInfo.put("nearestAirport", hospital.getNearestAirport());
        transitInfo.put("nearestBusStand", hospital.getNearestBusStand());
        transitInfo.put("wheelchairAccessible", hospital.isWheelchairAccessible());
        transitInfo.put("languagesSupported", hospital.getLanguagesSupported());
        transitInfo.put("lodgingGuidance", hospital.getLodgingGuidance());
        transitInfo.put("transitHelpDeskPhone", hospital.getTransitHelpDeskPhone());
        transitInfo.put("icuBedsAvailable", hospital.getIcuBedsAvailable());
        transitInfo.put("hasEmergencyServices", hospital.isHasEmergencyServices());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", transitInfo);

        return ResponseEntity.ok(response);
    }
}

