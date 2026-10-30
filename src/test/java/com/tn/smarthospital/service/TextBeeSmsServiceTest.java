package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.BookingRequest;
import com.tn.smarthospital.dto.SmsResult;
import com.tn.smarthospital.model.*;
import com.tn.smarthospital.repository.AppointmentRepository;
import com.tn.smarthospital.repository.AvailabilitySlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class TextBeeSmsServiceTest {

    private TextBeeSmsService smsService;
    private MockRestServiceServer mockServer;
    private RestClient.Builder restClientBuilder;

    private Appointment sampleAppointment;
    private User patientUser;
    private Doctor doctor;
    private Hospital hospital;
    private AvailabilitySlot slot;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder().baseUrl("https://api.textbee.dev/api/v1");
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();

        smsService = new TextBeeSmsService(
                "test-api-key-12345",
                "device-sample-id",
                "https://api.textbee.dev/api/v1",
                true,
                restClientBuilder.build()
        );

        // Setup entity models for appointment workflow testing
        patientUser = new User("Selvan Kumar", "patient@demo.tn.gov.in", "pass", "9876543210", Role.ROLE_PATIENT);
        patientUser.setId(10L);

        hospital = new Hospital("Demo City General Hospital", "Multi-Specialty", "Chennai", "Chennai",
                "Sample Address", "044-0000", "info@demo.local", "Cardiology", "Sample");
        hospital.setId(1L);

        doctor = new Doctor("Dr. K. Anitha", "Cardiology", "MBBS, MD", 10, hospital,
                "Tamil, English", "In-Person", 500.0, "Room 101");
        doctor.setId(2L);

        slot = new AvailabilitySlot(doctor, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(10, 30));
        slot.setId(3L);
        slot.setStatus(SlotStatus.AVAILABLE);

        sampleAppointment = new Appointment("TNSH-20261031-9999", patientUser, "Selvan Kumar",
                "9876543210", "patient@demo.tn.gov.in", doctor, hospital, slot,
                slot.getDate(), slot.getStartTime(), "Checkup");
        sampleAppointment.setId(100L);
    }

    // 1. Test SMS API Success
    @Test
    void testSendSms_Success() {
        mockServer.expect(requestTo("https://api.textbee.dev/api/v1/gateway/send-sms"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("x-api-key", "test-api-key-12345"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.recipients[0]").value("+919876543210"))
                .andExpect(jsonPath("$.deviceId").value("device-sample-id"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andRespond(withSuccess("{\"success\":true,\"data\":{\"messageId\":\"msg_123\"}}", MediaType.APPLICATION_JSON));

        SmsResult result = smsService.sendBookingNotification(sampleAppointment);

        mockServer.verify();
        assertTrue(result.isSuccess(), "Result should be successful");
        assertEquals("SENT", result.getStatus());
        assertTrue(result.getMaskedRecipient().contains("3210"));
    }

    // 2. Test Invalid API Key
    @Test
    void testSendSms_InvalidApiKey() {
        mockServer.expect(requestTo("https://api.textbee.dev/api/v1/gateway/send-sms"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withUnauthorizedRequest());

        SmsResult result = smsService.sendSms("9876543210", "Test message");

        mockServer.verify();
        assertFalse(result.isSuccess(), "Invalid key should result in failure");
        assertEquals("FAILED", result.getStatus());
        assertTrue(result.getMessage().contains("Authentication failure") || result.getMessage().contains("401"));
    }

    // 3. Test Network Failure
    @Test
    void testSendSms_NetworkFailure() {
        RestClient failingClient = RestClient.builder()
                .baseUrl("https://api.textbee.dev/api/v1")
                .requestFactory(new org.springframework.http.client.ClientHttpRequestFactory() {
                    @Override
                    public org.springframework.http.client.ClientHttpRequest createRequest(java.net.URI uri, HttpMethod httpMethod) {
                        throw new ResourceAccessException("Connection refused: connect to api.textbee.dev failed");
                    }
                })
                .build();

        TextBeeSmsService failingSmsService = new TextBeeSmsService(
                "test-api-key", "device-123", "https://api.textbee.dev/api/v1", true, failingClient);

        SmsResult result = failingSmsService.sendSms("9876543210", "Test message");

        assertFalse(result.isSuccess());
        assertEquals("FAILED", result.getStatus());
        assertTrue(result.getMessage().contains("Network connection"));
    }

    // 4. Test SMS Disabled
    @Test
    void testSendSms_Disabled() {
        TextBeeSmsService disabledService = new TextBeeSmsService(
                "test-api-key", "device-123", "https://api.textbee.dev/api/v1", false, restClientBuilder.build());

        SmsResult result = disabledService.sendSms("9876543210", "Test message");

        mockServer.verify();
        assertFalse(result.isSuccess());
        assertEquals("DISABLED", result.getStatus());
        assertTrue(result.getMessage().contains("disabled"));
    }

    // 5. Test Appointment Booking Despite SMS Failure
    @Test
    void testAppointmentBooking_DespiteSmsFailure() {
        TestTextBeeSmsService testSmsService = new TestTextBeeSmsService();
        testSmsService.bookingResult = SmsResult.failed("+91 ******3210", "Simulated gateway outage");

        AppointmentRepository appointmentRepo = createAppointmentRepoProxy();
        AvailabilitySlotRepository slotRepo = createSlotRepoProxy();
        NotificationService notificationService = createFakeNotificationService();

        AppointmentService appointmentService = new AppointmentService(
                appointmentRepo, slotRepo, notificationService, testSmsService);

        BookingRequest request = new BookingRequest();
        request.setSlotId(slot.getId());
        request.setPatientName("Selvan Kumar");
        request.setPatientPhone("9876543210");
        request.setPatientEmail("patient@demo.tn.gov.in");

        Appointment booked = appointmentService.bookAppointment(request, patientUser);

        // Assert appointment was successfully created and not rolled back
        assertNotNull(booked);
        assertEquals(AppointmentStatus.CONFIRMED, booked.getStatus());
        assertEquals("FAILED", booked.getSmsNotificationStatus());
        assertTrue(booked.getSmsNotificationDetails().contains("Simulated gateway outage"));
        assertFalse(booked.isSmsNotificationSent());

        // Verify slot was still marked BOOKED
        assertEquals(SlotStatus.BOOKED, slot.getStatus());
    }

    // 6. Test Appointment Cancellation Notification
    @Test
    void testAppointmentCancellationNotification() {
        TestTextBeeSmsService testSmsService = new TestTextBeeSmsService();
        testSmsService.cancellationResult = SmsResult.sent("+91 ******3210", "Cancellation SMS delivered");

        AppointmentRepository appointmentRepo = createAppointmentRepoProxy();
        AvailabilitySlotRepository slotRepo = createSlotRepoProxy();
        NotificationService notificationService = createFakeNotificationService();

        AppointmentService appointmentService = new AppointmentService(
                appointmentRepo, slotRepo, notificationService, testSmsService);

        appointmentService.cancelAppointment(sampleAppointment.getId(), "Patient requested", patientUser);

        assertEquals(AppointmentStatus.CANCELLED, sampleAppointment.getStatus());
        assertEquals(1, testSmsService.cancellationCalls);
        assertEquals("SENT", sampleAppointment.getSmsNotificationStatus());
    }

    // 7. Test Duplicate Notification Prevention
    @Test
    void testDuplicateNotificationPrevention() {
        TestTextBeeSmsService testSmsService = new TestTextBeeSmsService();
        testSmsService.bookingResult = SmsResult.sent("+91 ******3210", "Dispatched");

        AtomicBoolean slotAlreadyBooked = new AtomicBoolean(false);

        AppointmentRepository appointmentRepo = (AppointmentRepository) Proxy.newProxyInstance(
                AppointmentRepository.class.getClassLoader(),
                new Class<?>[]{AppointmentRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    if ("findById".equals(method.getName())) {
                        return Optional.of(sampleAppointment);
                    }
                    if ("findByBookingReference".equals(method.getName())) {
                        return Optional.empty();
                    }
                    if ("existsByPatientIdAndSlotIdAndStatusNot".equals(method.getName())) {
                        // Return true if previously booked to simulate duplicate protection check
                        return slotAlreadyBooked.get();
                    }
                    return null;
                }
        );

        AvailabilitySlotRepository slotRepo = (AvailabilitySlotRepository) Proxy.newProxyInstance(
                AvailabilitySlotRepository.class.getClassLoader(),
                new Class<?>[]{AvailabilitySlotRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.of(slot);
                    }
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    return null;
                }
        );

        NotificationService notificationService = createFakeNotificationService();

        AppointmentService appointmentService = new AppointmentService(
                appointmentRepo, slotRepo, notificationService, testSmsService);

        BookingRequest request = new BookingRequest();
        request.setSlotId(slot.getId());
        request.setPatientName("Selvan Kumar");
        request.setPatientPhone("9876543210");

        // 1st booking succeeds
        Appointment firstBooked = appointmentService.bookAppointment(request, patientUser);
        assertNotNull(firstBooked);
        assertEquals(1, testSmsService.bookingCalls);
        assertTrue(firstBooked.isSmsNotificationSent());

        // Now slot status is BOOKED and user already has an appointment
        slotAlreadyBooked.set(true);

        // 2nd duplicate booking attempt fails and does NOT trigger a second SMS notification
        assertThrows(IllegalStateException.class, () -> {
            appointmentService.bookAppointment(request, patientUser);
        });

        // Ensure SMS was NOT sent again
        assertEquals(1, testSmsService.bookingCalls, "Duplicate submission must not trigger a second SMS");
    }

    // Helper stubs
    private AppointmentRepository createAppointmentRepoProxy() {
        return (AppointmentRepository) Proxy.newProxyInstance(
                AppointmentRepository.class.getClassLoader(),
                new Class<?>[]{AppointmentRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    if ("findById".equals(method.getName())) {
                        return Optional.of(sampleAppointment);
                    }
                    if ("findByBookingReference".equals(method.getName())) {
                        return Optional.empty();
                    }
                    if ("existsByPatientIdAndSlotIdAndStatusNot".equals(method.getName())) {
                        return false;
                    }
                    return null;
                }
        );
    }

    private AvailabilitySlotRepository createSlotRepoProxy() {
        return (AvailabilitySlotRepository) Proxy.newProxyInstance(
                AvailabilitySlotRepository.class.getClassLoader(),
                new Class<?>[]{AvailabilitySlotRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        return Optional.of(slot);
                    }
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    return null;
                }
        );
    }

    private NotificationService createFakeNotificationService() {
        return new NotificationService(null) {
            @Override
            public Notification createNotification(User user, String message, String type) {
                return new Notification(user, message, type);
            }
        };
    }

    static class TestTextBeeSmsService extends TextBeeSmsService {
        int bookingCalls = 0;
        int cancellationCalls = 0;
        SmsResult bookingResult = SmsResult.sent("+91 ******3210", "Success");
        SmsResult cancellationResult = SmsResult.sent("+91 ******3210", "Success");

        public TestTextBeeSmsService() {
            super("test-key", "device-1", "https://api.textbee.dev/api/v1", true, (RestClient) null);
        }

        @Override
        public SmsResult sendBookingNotification(Appointment appointment) {
            bookingCalls++;
            return bookingResult;
        }

        @Override
        public SmsResult sendCancellationNotification(Appointment appointment) {
            cancellationCalls++;
            return cancellationResult;
        }
    }
}
