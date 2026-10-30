package com.tn.smarthospital.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextBeeSendSmsRequest {

    private List<String> recipients = new ArrayList<>();
    private String message;
    private String deviceId;

    public TextBeeSendSmsRequest() {
    }

    public TextBeeSendSmsRequest(String recipient, String message, String deviceId) {
        if (recipient != null) {
            this.recipients = Collections.singletonList(recipient);
        }
        this.message = message;
        if (deviceId != null && !deviceId.trim().isEmpty()) {
            this.deviceId = deviceId.trim();
        }
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

