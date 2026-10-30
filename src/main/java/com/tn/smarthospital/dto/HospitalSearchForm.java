package com.tn.smarthospital.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class HospitalSearchForm {

    private String district;
    private String city;
    private String specialty;
    private String hospitalType;
    private String query;
    private Boolean hasEmergency;
    private Boolean hasIcu;
    private String sortBy; // "name", "district", "icuBeds", "doctors"

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate appointmentDate;

    public HospitalSearchForm() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Boolean getHasEmergency() {
        return hasEmergency;
    }

    public void setHasEmergency(Boolean hasEmergency) {
        this.hasEmergency = hasEmergency;
    }

    public Boolean getHasIcu() {
        return hasIcu;
    }

    public void setHasIcu(Boolean hasIcu) {
        this.hasIcu = hasIcu;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public void setHospitalType(String hospitalType) {
        this.hospitalType = hospitalType;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}

