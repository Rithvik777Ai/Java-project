package com.tn.smarthospital.dto;

public class SmsResult {

    private final boolean success;
    private final String status; // "SENT", "DISABLED", "FAILED"
    private final String message;
    private final String maskedRecipient;

    public SmsResult(boolean success, String status, String message, String maskedRecipient) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.maskedRecipient = maskedRecipient;
    }

    public static SmsResult sent(String maskedRecipient, String note) {
        return new SmsResult(true, "SENT", note != null ? note : "SMS dispatched via TextBee gateway", maskedRecipient);
    }

    public static SmsResult disabled(String maskedRecipient) {
        return new SmsResult(false, "DISABLED", "SMS notifications are currently disabled in configuration", maskedRecipient);
    }

    public static SmsResult failed(String maskedRecipient, String errorMessage) {
        return new SmsResult(false, "FAILED", errorMessage != null ? errorMessage : "SMS delivery failed", maskedRecipient);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getMaskedRecipient() {
        return maskedRecipient;
    }

    @Override
    public String toString() {
        return "SmsResult{" +
                "success=" + success +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", maskedRecipient='" + maskedRecipient + '\'' +
                '}';
    }
}

