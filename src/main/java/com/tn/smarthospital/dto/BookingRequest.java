package com.tn.smarthospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Please select an appointment slot")
    private Long slotId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String patientName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9")
    private String patientPhone;

    @Email(message = "Please enter a valid email address")
    private String patientEmail;

    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    private boolean confirmDemoNotice;

    public BookingRequest() {
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isConfirmDemoNotice() {
        return confirmDemoNotice;
    }

    public void setConfirmDemoNotice(boolean confirmDemoNotice) {
        this.confirmDemoNotice = confirmDemoNotice;
    }
}

