package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.SmsResult;
import com.tn.smarthospital.dto.TextBeeSendSmsRequest;
import com.tn.smarthospital.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Service
public class TextBeeSmsService {

    private static final Logger logger = LoggerFactory.getLogger(TextBeeSmsService.class);

    private final String apiKey;
    private final String deviceId;
    private final String apiUrl;
    private final boolean enabled;
    private final RestClient restClient;

    @Autowired
    public TextBeeSmsService(
            @Value("${textbee.api.key:}") String apiKey,
            @Value("${textbee.device.id:}") String deviceId,
            @Value("${textbee.api.url:https://api.textbee.dev/api/v1}") String apiUrl,
            @Value("${textbee.enabled:false}") boolean enabled,
            RestClient.Builder restClientBuilder) {

        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.deviceId = deviceId != null ? deviceId.trim() : "";
        this.apiUrl = apiUrl != null && !apiUrl.trim().isEmpty() ? apiUrl.trim() : "https://api.textbee.dev/api/v1";
        this.enabled = enabled;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        this.restClient = restClientBuilder
                .baseUrl(this.apiUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Constructor specifically for unit testing with custom RestClient
     */
    public TextBeeSmsService(String apiKey, String deviceId, String apiUrl, boolean enabled, RestClient restClient) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.deviceId = deviceId != null ? deviceId.trim() : "";
        this.apiUrl = apiUrl != null && !apiUrl.trim().isEmpty() ? apiUrl.trim() : "https://api.textbee.dev/api/v1";
        this.enabled = enabled;
        this.restClient = restClient;
    }

    /**
     * Send an appointment booking confirmation SMS.
     */
    public SmsResult sendBookingNotification(Appointment appointment) {
        if (appointment == null) {
            return SmsResult.failed("Unknown", "Appointment record is null");
        }

        String message = "Hello " + appointment.getPatientName() + ", your demo appointment has been booked successfully.\n\n" +
                "Doctor: " + appointment.getDoctor().getName() + " " +
                "Hospital: " + appointment.getHospital().getName() + " " +
                "Date: " + appointment.getFormattedDate() + " " +
                "Time: " + appointment.getFormattedTime() + "\n\n" +
                "Booking Reference: " + appointment.getBookingReference() + "\n\n" +
                "Important: This is a demo booking and is NOT confirmed with the hospital. Please verify directly with the hospital.\n\n" +
                "Tamil Nadu Smart Hospital";

        return sendSms(appointment.getPatientPhone(), message);
    }

    /**
     * Send an appointment cancellation SMS.
     */
    public SmsResult sendCancellationNotification(Appointment appointment) {
        if (appointment == null) {
            return SmsResult.failed("Unknown", "Appointment record is null");
        }

        String message = "Hello " + appointment.getPatientName() + ", your demo appointment has been cancelled.\n\n" +
                "Booking Reference: " + appointment.getBookingReference() + "\n\n" +
                "Tamil Nadu Smart Hospital";

        return sendSms(appointment.getPatientPhone(), message);
    }

    /**
     * Send an appointment status update SMS.
     */
    public SmsResult sendStatusUpdateNotification(Appointment appointment, String newStatus) {
        if (appointment == null) {
            return SmsResult.failed("Unknown", "Appointment record is null");
        }

        String message = "Hello " + appointment.getPatientName() + ", your demo appointment status has been updated to " + newStatus + ".\n\n" +
                "Doctor: " + appointment.getDoctor().getName() + " " +
                "Hospital: " + appointment.getHospital().getName() + " " +
                "Date: " + appointment.getFormattedDate() + "\n\n" +
                "Booking Reference: " + appointment.getBookingReference() + "\n\n" +
                "Important: This is a demo appointment and is NOT confirmed with the hospital.\n\n" +
                "Tamil Nadu Smart Hospital";

        return sendSms(appointment.getPatientPhone(), message);
    }

    /**
     * Send a test SMS initiated from the Admin dashboard.
     */
    public SmsResult sendTestSms(String recipientPhone, String customMessage) {
        String msg = (customMessage != null && !customMessage.trim().isEmpty())
                ? customMessage.trim()
                : "[Tamil Nadu Smart Hospital Demo] Test diagnostic SMS from admin console. TextBee gateway verified.";
        return sendSms(recipientPhone, msg);
    }

    /**
     * Core SMS transmission method via TextBee.dev API.
     * Never throws exceptions out to the caller.
     */
    public SmsResult sendSms(String rawPhone, String messageText) {
        String masked = maskPhoneNumber(rawPhone);

        // 1. Check if disabled in configuration
        if (!enabled) {
            logger.info("TextBee SMS gateway is disabled (textbee.enabled=false). Skipping SMS to {}", masked);
            return SmsResult.disabled(masked);
        }

        // 2. Validate API Key
        if (apiKey.isEmpty()) {
            logger.warn("TextBee SMS gateway failed: API key is not configured.");
            return SmsResult.failed(masked, "TextBee API key is not configured");
        }

        // 3. Format Phone to E.164
        String e164Phone = formatToE164(rawPhone);
        if (e164Phone == null || e164Phone.isEmpty()) {
            logger.warn("Invalid phone number format provided for SMS: {}", masked);
            return SmsResult.failed(masked, "Invalid recipient mobile number format");
        }

        // 4. Construct Request Payload according to official TextBee schema
        TextBeeSendSmsRequest payload = new TextBeeSendSmsRequest(
                e164Phone,
                messageText,
                !deviceId.isEmpty() ? deviceId : null
        );

        try {
            logger.info("Sending SMS via TextBee to recipient: {} (endpoint: /gateway/send-sms)", masked);

            String responseBody = restClient.post()
                    .uri("/gateway/send-sms")
                    .header("x-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        int status = response.getStatusCode().value();
                        if (status == 401 || status == 403) {
                            throw new HttpClientErrorException(response.getStatusCode(), "Authentication failure: Invalid or expired TextBee API key");
                        }
                        throw new HttpClientErrorException(response.getStatusCode(), "TextBee gateway returned HTTP " + status);
                    })
                    .body(String.class);

            logger.info("SMS successfully accepted by TextBee gateway for recipient: {}", masked);
            return SmsResult.sent(masked, "SMS accepted by gateway for delivery to " + masked);

        } catch (HttpClientErrorException ex) {
            logger.error("TextBee HTTP client error [{}]: {}", ex.getStatusCode(), ex.getStatusText());
            return SmsResult.failed(masked, "Gateway rejected request: " + ex.getStatusText());
        } catch (HttpServerErrorException ex) {
            logger.error("TextBee server error [{}]: {}", ex.getStatusCode(), ex.getStatusText());
            return SmsResult.failed(masked, "TextBee gateway server error (" + ex.getStatusCode() + ")");
        } catch (ResourceAccessException ex) {
            logger.error("TextBee network/timeout failure: {}", ex.getMessage());
            return SmsResult.failed(masked, "Network connection or timeout communicating with TextBee gateway");
        } catch (Exception ex) {
            logger.error("Unexpected error during TextBee SMS dispatch: {}", ex.getMessage());
            return SmsResult.failed(masked, "Failed to dispatch SMS: " + ex.getMessage());
        }
    }

    /**
     * Normalizes a phone number to standard E.164 international format (+91 for India).
     */
    public String formatToE164(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9+]", "");
        if (digits.isEmpty()) return null;

        if (digits.startsWith("+")) {
            return digits;
        }

        // Standard 10-digit Indian mobile number
        if (digits.length() == 10 && digits.matches("^[6-9]\\d{9}$")) {
            return "+91" + digits;
        }

        // Already has 91 country code without +
        if (digits.length() == 12 && digits.startsWith("91")) {
            return "+" + digits;
        }

        return "+" + digits;
    }

    /**
     * Safely masks a phone number for logs and user interfaces (e.g. +91 ******3210).
     */
    public String maskPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "N/A";
        }
        String clean = phone.replaceAll("[^0-9+]", "");
        if (clean.length() <= 4) {
            return "****";
        }
        int last4Index = clean.length() - 4;
        String prefix = clean.startsWith("+91") ? "+91 " : "";
        String last4 = clean.substring(last4Index);
        return prefix + "******" + last4;
    }

    /**
     * Masked display of API key for Admin UI.
     * Never outputs full API key.
     */
    public String getMaskedApiKey() {
        if (apiKey.isEmpty()) {
            return "Not Configured";
        }
        if (apiKey.length() <= 8) {
            return "••••••••";
        }
        String prefix = apiKey.substring(0, Math.min(4, apiKey.length()));
        String suffix = apiKey.substring(apiKey.length() - 4);
        return prefix + "••••••••" + suffix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isConfigured() {
        return !apiKey.isEmpty();
    }

    public String getDeviceId() {
        return !deviceId.isEmpty() ? deviceId : "Default Assigned Gateway Device";
    }

    public String getApiUrl() {
        return apiUrl;
    }
}
