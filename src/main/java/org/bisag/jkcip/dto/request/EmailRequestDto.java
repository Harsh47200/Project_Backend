package org.bisag.jkcip.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequestDto {
    @Email
    @NotBlank
    private String to;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    private boolean includeGoogleMeet = true; // default true

    // getters & setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isIncludeGoogleMeet() { return includeGoogleMeet; }
    public void setIncludeGoogleMeet(boolean includeGoogleMeet) { this.includeGoogleMeet = includeGoogleMeet; }
}
