package com.tn.smarthospital.controller.api;

import com.tn.smarthospital.dto.BookingRequest;
import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.Role;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.service.AppointmentService;
import com.tn.smarthospital.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public AppointmentApiController(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    /**
     * Book a new demo appointment with CuraTransit travel assistance.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> bookAppointment(@Valid @RequestBody BookingRequest request) {
        User currentUser = userService.getCurrentUser().orElseGet(() -> {
            // For demo REST API clients without session, map to demo patient
            return userService.findByEmail("patient@demo.tn.gov.in")
                    .orElseGet(() -> userService.createUserDirectly(
                            request.getPatientName(),
                            request.getPatientEmail() != null ? request.getPatientEmail() : "guest.patient@demo.tn.gov.in",
                            "demo123",
                            request.getPatientPhone(),
                            Role.ROLE_PATIENT
                    ));
        });

        Appointment appointment = appointmentService.bookAppointment(request, currentUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Your demo appointment has been saved successfully.");
        response.put("bookingReference", appointment.getBookingReference());
        response.put("doctorName", appointment.getDoctor().getName());
        response.put("hospitalName", appointment.getHospital().getName());
        response.put("date", appointment.getFormattedDate());
        response.put("time", appointment.getFormattedTime());
        response.put("smsNotificationStatus", appointment.getSmsNotificationStatus());
        response.put("needsWheelchair", appointment.isNeedsWheelchair());
        response.put("needsLanguageSupport", appointment.isNeedsLanguageSupport());
        response.put("preferredLanguage", appointment.getPreferredLanguage());
        response.put("isOutstationPatient", appointment.isOutstationPatient());
        response.put("travelOrigin", appointment.getTravelOrigin());
        response.put("disclaimer", "Demo booking — not confirmed with the hospital. SMS notifications are informational only.");
        response.put("data", appointment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get patient's appointments.
     */
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyAppointments() {
        User currentUser = userService.getCurrentUser().orElseGet(() ->
                userService.findByEmail("patient@demo.tn.gov.in").orElse(null)
        );

        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Appointment> appointments = appointmentService.getPatientAppointments(currentUser.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", appointments.size());
        response.put("data", appointments);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an appointment.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        User currentUser = userService.getCurrentUser().orElseGet(() ->
                userService.findByEmail("patient@demo.tn.gov.in").orElse(null)
        );

        if (currentUser == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : "Patient requested cancellation via API";
        appointmentService.cancelAppointment(id, reason, currentUser);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Appointment cancelled successfully");
        response.put("appointmentId", id);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve appointment by booking reference.
     */
    @GetMapping("/{reference}")
    public ResponseEntity<Map<String, Object>> getByReference(@PathVariable String reference) {
        Appointment appointment = appointmentService.getAppointmentByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with reference: " + reference));

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", appointment);

        return ResponseEntity.ok(response);
    }
}

