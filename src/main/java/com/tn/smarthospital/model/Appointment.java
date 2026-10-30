package com.tn.smarthospital.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String patientPhone;

    private String patientEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private AvailabilitySlot slot;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false)
    private boolean isDemo = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String cancellationReason;

    @Column(length = 50)
    private String smsNotificationStatus = "DISABLED";

    @Column(length = 255)
    private String smsNotificationDetails;

    @Column(nullable = false)
    private boolean smsNotificationSent = false;

    public Appointment() {
    }

    public Appointment(String bookingReference, User patient, String patientName, String patientPhone,
                       String patientEmail, Doctor doctor, Hospital hospital, AvailabilitySlot slot,
                       LocalDate date, LocalTime time, String reason) {
        this.bookingReference = bookingReference;
        this.patient = patient;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.patientEmail = patientEmail;
        this.doctor = doctor;
        this.hospital = hospital;
        this.slot = slot;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = AppointmentStatus.CONFIRMED;
        this.isDemo = true;
        this.createdAt = LocalDateTime.now();
    }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"));
    }

    public String getFormattedTime() {
        return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
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

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public AvailabilitySlot getSlot() {
        return slot;
    }

    public void setSlot(AvailabilitySlot slot) {
        this.slot = slot;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getSmsNotificationStatus() {
        return smsNotificationStatus;
    }

    public void setSmsNotificationStatus(String smsNotificationStatus) {
        this.smsNotificationStatus = smsNotificationStatus;
    }

    public String getSmsNotificationDetails() {
        return smsNotificationDetails;
    }

    public void setSmsNotificationDetails(String smsNotificationDetails) {
        this.smsNotificationDetails = smsNotificationDetails;
    }

    public boolean isSmsNotificationSent() {
        return smsNotificationSent;
    }

    public void setSmsNotificationSent(boolean smsNotificationSent) {
        this.smsNotificationSent = smsNotificationSent;
    }
}

