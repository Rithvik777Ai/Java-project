package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.HospitalForm;
import com.tn.smarthospital.dto.HospitalSearchForm;
import com.tn.smarthospital.model.Hospital;
import com.tn.smarthospital.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public List<Hospital> getAllActiveHospitals() {
        return hospitalRepository.findByActiveTrue();
    }

    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> getHospitalById(Long id) {
        return hospitalRepository.findById(id);
    }

    public List<Hospital> getHospitalsForComparison(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return hospitalRepository.findByIdInAndActiveTrue(ids);
    }

    public List<Hospital> searchHospitals(HospitalSearchForm form) {
        String district = (form.getDistrict() != null && !form.getDistrict().trim().isEmpty()) ? form.getDistrict().trim() : null;
        String city = (form.getCity() != null && !form.getCity().trim().isEmpty()) ? form.getCity().trim() : null;
        String hospitalType = (form.getHospitalType() != null && !form.getHospitalType().trim().isEmpty()) ? form.getHospitalType().trim() : null;
        String specialty = (form.getSpecialty() != null && !form.getSpecialty().trim().isEmpty()) ? form.getSpecialty().trim() : null;
        String query = (form.getQuery() != null && !form.getQuery().trim().isEmpty()) ? form.getQuery().trim() : null;
        Boolean hasEmergency = form.getHasEmergency();
        Boolean hasIcu = form.getHasIcu();

        List<Hospital> list = hospitalRepository.searchHospitals(district, city, hospitalType, specialty, hasEmergency, hasIcu, query);

        // Apply Sorting
        String sortBy = form.getSortBy();
        if ("district".equalsIgnoreCase(sortBy)) {
            list.sort((a, b) -> a.getDistrict().compareToIgnoreCase(b.getDistrict()));
        } else if ("icuBeds".equalsIgnoreCase(sortBy)) {
            list.sort((a, b) -> Integer.compare(b.getIcuBedsAvailable(), a.getIcuBedsAvailable()));
        } else if ("doctors".equalsIgnoreCase(sortBy)) {
            list.sort((a, b) -> Integer.compare(b.getDoctors().size(), a.getDoctors().size()));
        } else {
            // Default: by Name
            list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        }

        return list;
    }

    public List<String> getAllDistricts() {
        return hospitalRepository.findDistinctDistricts();
    }

    public List<String> getAllHospitalTypes() {
        return hospitalRepository.findDistinctHospitalTypes();
    }

    @Transactional
    public Hospital saveHospital(HospitalForm form) {
        Hospital hospital;
        if (form.getId() != null) {
            hospital = hospitalRepository.findById(form.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Hospital not found with id: " + form.getId()));
        } else {
            hospital = new Hospital();
            hospital.setDemo(true);
            hospital.setState("Tamil Nadu");
        }

        hospital.setName(form.getName().trim());
        hospital.setHospitalType(form.getHospitalType().trim());
        hospital.setDistrict(form.getDistrict().trim());
        hospital.setCity(form.getCity().trim());
        hospital.setAddress(form.getAddress().trim());
        hospital.setPhone(form.getPhone() != null ? form.getPhone().trim() : "Demo Contact");
        hospital.setEmail(form.getEmail() != null ? form.getEmail().trim() : "info@sample.local");
        hospital.setDepartments(form.getDepartments() != null ? form.getDepartments().trim() : "");
        hospital.setDescription(form.getDescription() != null ? form.getDescription().trim() : "");
        hospital.setActive(form.isActive());

        hospital.setHasEmergencyServices(form.isHasEmergencyServices());
        hospital.setHasIcu(form.isHasIcu());
        hospital.setIcuBedsAvailable(form.getIcuBedsAvailable());
        hospital.setWheelchairAccessible(form.isWheelchairAccessible());
        hospital.setLanguagesSupported(form.getLanguagesSupported() != null ? form.getLanguagesSupported().trim() : "Tamil, English");
        hospital.setNearestRailwayStation(form.getNearestRailwayStation() != null ? form.getNearestRailwayStation().trim() : "");
        hospital.setNearestAirport(form.getNearestAirport() != null ? form.getNearestAirport().trim() : "");
        hospital.setNearestBusStand(form.getNearestBusStand() != null ? form.getNearestBusStand().trim() : "");
        hospital.setLodgingGuidance(form.getLodgingGuidance() != null ? form.getLodgingGuidance().trim() : "");
        hospital.setTransitHelpDeskPhone(form.getTransitHelpDeskPhone() != null ? form.getTransitHelpDeskPhone().trim() : "");

        return hospitalRepository.save(hospital);
    }

    @Transactional
    public void toggleHospitalStatus(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + id));
        hospital.setActive(!hospital.isActive());
        hospitalRepository.save(hospital);
    }

    public long getTotalHospitalsCount() {
        return hospitalRepository.count();
    }
}

