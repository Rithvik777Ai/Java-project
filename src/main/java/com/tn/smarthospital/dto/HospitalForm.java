package com.tn.smarthospital.dto;

import jakarta.validation.constraints.NotBlank;

public class HospitalForm {

    private Long id;

    @NotBlank(message = "Hospital name is required")
    private String name;

    @NotBlank(message = "Hospital type is required")
    private String hospitalType;

    private String state = "Tamil Nadu";

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    private String address;

    private String phone;
    private String email;
    private String departments;
    private String description;
    private boolean active = true;

    // CuraTransit Emergency, ICU & Transit Fields
    private boolean hasEmergencyServices = true;
    private boolean hasIcu = true;
    private int icuBedsAvailable = 10;
    private boolean wheelchairAccessible = true;
    private String languagesSupported = "Tamil, English";
    private String nearestRailwayStation;
    private String nearestAirport;
    private String nearestBusStand;
    private String lodgingGuidance;
    private String transitHelpDeskPhone;

    public HospitalForm() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

