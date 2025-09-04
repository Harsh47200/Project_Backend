package org.bisag.jkcip.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SmsRequestDto {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String message;

    // Optional: carrier gateway or email-to-sms target
    private String carrierGateway; // e.g. {phone}@gateway.example.com

    // getters & setters
    // (or use Lombok @Data)
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCarrierGateway() { return carrierGateway; }
    public void setCarrierGateway(String carrierGateway) { this.carrierGateway = carrierGateway; }
}
