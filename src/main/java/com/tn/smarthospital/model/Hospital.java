package com.tn.smarthospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String hospitalType; // e.g. Multi-Specialty, Super Specialty, General Hospital, Community Clinic

    @Column(nullable = false)
    private String state = "Tamil Nadu";

    @NotBlank
    @Column(nullable = false)
    private String district;

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String address;

    private String phone;

    private String email;

    @Column(length = 1000)
    private String departments; // Comma separated list of departments

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean isDemo = true;

    @Column(nullable = false)
    private boolean active = true;

    // CuraTransit Emergency, ICU & Travel Assistance Fields
    @Column(nullable = false)
    private boolean hasEmergencyServices = true;

    @Column(nullable = false)
    private boolean hasIcu = true;

    @Column(nullable = false)
    private int icuBedsAvailable = 12;

    @Column(nullable = false)
    private boolean wheelchairAccessible = true;

    @Column(length = 255)
    private String languagesSupported = "Tamil, English, Hindi, Telugu, Malayalam";

    @Column(length = 255)
    private String nearestRailwayStation;

    @Column(length = 255)
    private String nearestAirport;

    @Column(length = 255)
    private String nearestBusStand;

    @Column(length = 500)
    private String lodgingGuidance;

    @Column(length = 100)
    private String transitHelpDeskPhone;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hospital", "hibernateLazyInitializer", "handler"})
    private List<Doctor> doctors = new ArrayList<>();

    public Hospital() {
    }

    public Hospital(String name, String hospitalType, String district, String city, String address,
                    String phone, String email, String departments, String description) {
        this.name = name;
        this.hospitalType = hospitalType;
        this.state = "Tamil Nadu";
        this.district = district;
        this.city = city;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.departments = departments;
        this.description = description;
        this.isDemo = true;
        this.active = true;
    }

    public List<String> getDepartmentList() {
        if (departments == null || departments.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(departments.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospitalType() {
        return hospitalType;
    }

    public void setHospitalType(String hospitalType) {
        this.hospitalType = hospitalType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public boolean isHasEmergencyServices() {
        return hasEmergencyServices;
    }

    public void setHasEmergencyServices(boolean hasEmergencyServices) {
        this.hasEmergencyServices = hasEmergencyServices;
    }

    public boolean isHasIcu() {
        return hasIcu;
    }

    public void setHasIcu(boolean hasIcu) {
        this.hasIcu = hasIcu;
    }

    public int getIcuBedsAvailable() {
        return icuBedsAvailable;
    }

    public void setIcuBedsAvailable(int icuBedsAvailable) {
        this.icuBedsAvailable = icuBedsAvailable;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public String getLanguagesSupported() {
        return languagesSupported;
    }

    public void setLanguagesSupported(String languagesSupported) {
        this.languagesSupported = languagesSupported;
    }

    public String getNearestRailwayStation() {
        return nearestRailwayStation;
    }

    public void setNearestRailwayStation(String nearestRailwayStation) {
        this.nearestRailwayStation = nearestRailwayStation;
    }

    public String getNearestAirport() {
        return nearestAirport;
    }

    public void setNearestAirport(String nearestAirport) {
        this.nearestAirport = nearestAirport;
    }

    public String getNearestBusStand() {
        return nearestBusStand;
    }

    public void setNearestBusStand(String nearestBusStand) {
        this.nearestBusStand = nearestBusStand;
    }

    public String getLodgingGuidance() {
        return lodgingGuidance;
    }

    public void setLodgingGuidance(String lodgingGuidance) {
        this.lodgingGuidance = lodgingGuidance;
    }

    public String getTransitHelpDeskPhone() {
        return transitHelpDeskPhone;
    }

    public void setTransitHelpDeskPhone(String transitHelpDeskPhone) {
        this.transitHelpDeskPhone = transitHelpDeskPhone;
    }
}

