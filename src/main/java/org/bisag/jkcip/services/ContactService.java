package org.bisag.jkcip.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ContactService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String TARGET_PHONE = "+917990178938";
    private static final String TARGET_EMAIL = "harsh31797@gmail.com";
    private static final String SMS_API_URL = "https://www.fast2sms.com/dev/bulkV2";
    // Get free API key from fast2sms.com
    private static final String SMS_API_KEY = "ucdWxndxNbchiRLdByoMmhgy6321rVfXm2BbZVYua8ogGWrMrYVZcra5OEFY";

        // public boolean sendServiceRequestSMS() {
        //     try {
        //         String message = "🔧 MACHINERY SERVICE REQUEST\n" +
        //                     "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\n" +
        //                     "Issue: General machinery maintenance required\n" +
        //                     "Status: New service request received\n" +
        //                     "Please respond ASAP";

        //         return sendSMS(TARGET_PHONE, message);
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //         return false;
        //     }
        // }

   public boolean sendSMSWithHeader(String phoneNumber, String messageText) {
    try {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        
        RequestBody formBody = new FormBody.Builder()
                .add("sender_id", "FTWSMS")
                .add("message", messageText)
                .add("language", "english")
                .add("route", "p")
                .add("numbers", phoneNumber.replace("+91", ""))
                .build();

        Request request = new Request.Builder()
                .url(SMS_API_URL)
                .addHeader("Authorization", SMS_API_KEY) // Try this instead
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBodyString = "";
            if (response.body() != null) {
                responseBodyString = response.body().string();
            }
            
            System.out.println("SMS API Response Code: " + response.code());
            System.out.println("SMS API Response Body: " + responseBodyString);
            
            return response.isSuccessful() && responseBodyString.contains("\"return\":true");
        }
        
    } catch (IOException e) {
        System.err.println("SMS sending failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Alternative approach - manually closing response
private boolean sendSMSAlternative(String phoneNumber, String messageText) {
    Response response = null;
    try {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        
        RequestBody formBody = new FormBody.Builder()
                .add("authorization", SMS_API_KEY)
                .add("sender_id", "FTWSMS")
                .add("message", messageText)
                .add("language", "english")
                .add("route", "p")
                .add("numbers", phoneNumber.replace("+91", ""))
                .build();

        Request request = new Request.Builder()
                .url(SMS_API_URL)
                .post(formBody)
                .build();

        response = client.newCall(request).execute();
        
        // Read and log response
        String responseBodyString = "";
        if (response.body() != null) {
            responseBodyString = response.body().string();
        }
        
        System.out.println("SMS API Response Code: " + response.code());
        System.out.println("SMS API Response Body: " + responseBodyString);
        
        return response.isSuccessful();
        
    } catch (IOException e) {
        System.err.println("SMS sending failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        // Always close the response in the finally block
        if (response != null) {
            response.close();
        }
    }
}

// Enhanced service method with better error handling
public boolean sendServiceRequestSMS() {
    try {
        // Validate configuration first
        if (SMS_API_KEY == null || SMS_API_KEY.trim().isEmpty()) {
            System.err.println("SMS_API_KEY is not configured");
            return false;
        }
        
        if (SMS_API_URL == null || SMS_API_URL.trim().isEmpty()) {
            System.err.println("SMS_API_URL is not configured");
            return false;
        }
        
        if (TARGET_PHONE == null || TARGET_PHONE.trim().isEmpty()) {
            System.err.println("TARGET_PHONE is not configured");
            return false;
        }

        String message = "🔧 MACHINERY SERVICE REQUEST\n" +
                       "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\n" +
                       "Issue: General machinery maintenance required\n" +
                       "Status: New service request received\n" +
                       "Please respond ASAP";

        System.out.println("Sending SMS to: " + TARGET_PHONE);
        System.out.println("Message length: " + message.length() + " characters");

        return sendSMSWithHeader(TARGET_PHONE, message);
    } catch (Exception e) {
        System.err.println("Service request SMS failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}


    public boolean sendEmergencyEmail() {
        try {
            String meetingLink = generateGoogleMeetLink();
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(TARGET_EMAIL);
            message.setFrom(fromEmail);
            message.setSubject("🚨 EMERGENCY - Heavy Machinery Assistance Required");
            
            String emailBody = "EMERGENCY ASSISTANCE REQUEST\n\n" +
                             "Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n" +
                             "Priority: HIGH\n" +
                             "Type: Emergency machinery support needed\n\n" +
                             "IMMEDIATE ACTION REQUIRED:\n" +
                             "- Heavy machinery emergency situation\n" +
                             "- Immediate technical support needed\n" +
                             "- Please contact customer ASAP\n\n" +
                             "Google Meet Link for Emergency Support:\n" +
                             meetingLink + "\n\n" +
                             "This is an automated emergency notification.\n" +
                             "Please respond immediately.";
            
            message.setText(emailBody);
            mailSender.send(message);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmergencyEmailToUser(String userEmail, String userName, String userPhone, String emergencyDetails) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setFrom(fromEmail);
        message.setSubject("🚨 Emergency Support - We've Received Your Request");
        
        // Generate a unique reference ID for tracking
        String referenceId = "EMG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("EMERGENCY SUPPORT REQUEST RECEIVED\n\n");
        emailBody.append("Reference ID: ").append(referenceId).append("\n");
        emailBody.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        emailBody.append("Status: Request received and being processed\n\n");
        
        if (userName != null && !userName.trim().isEmpty()) {
            emailBody.append("Name: ").append(userName).append("\n");
        }
        
        if (userPhone != null && !userPhone.trim().isEmpty()) {
            emailBody.append("Phone: ").append(userPhone).append("\n");
        }
        
        emailBody.append("Email: ").append(userEmail).append("\n\n");
        
        if (emergencyDetails != null && !emergencyDetails.trim().isEmpty()) {
            emailBody.append("Emergency Details:\n").append(emergencyDetails).append("\n\n");
        }
        
        emailBody.append("WHAT HAPPENS NEXT:\n");
        emailBody.append("• Our emergency response team has been notified\n");
        emailBody.append("• You will receive a call within 15-30 minutes\n");
        emailBody.append("• Keep your phone available for immediate assistance\n");
        emailBody.append("• If this is life-threatening, please call emergency services immediately\n\n");
        
        emailBody.append("Emergency Hotline: +91-7990178938\n");
        emailBody.append("Email Support: harsh31797@gmail.com\n\n");
        
        emailBody.append("Thank you for contacting our emergency support.\n");
        emailBody.append("Help is on the way!\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("Emergency Response Team");
        
        message.setText(emailBody.toString());
        
        // Send confirmation email to user
        mailSender.send(message);
        
        // Also send notification to your team (optional)
        sendEmergencyNotificationToTeam(userEmail, userName, userPhone, emergencyDetails, referenceId);
        
        return true;
    } catch (Exception e) {
        System.err.println("Emergency email sending failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Optional: Send notification to your team about the emergency request
private boolean sendEmergencyNotificationToTeam(String userEmail, String userName, String userPhone, String emergencyDetails, String referenceId) {
    try {
        SimpleMailMessage teamMessage = new SimpleMailMessage();
        teamMessage.setTo("harsh31797@gmail.com"); // Your team email
        teamMessage.setFrom(fromEmail);
        teamMessage.setSubject("🚨 NEW EMERGENCY REQUEST - " + referenceId);
        
        StringBuilder teamEmailBody = new StringBuilder();
        teamEmailBody.append("NEW EMERGENCY REQUEST RECEIVED\n\n");
        teamEmailBody.append("Reference ID: ").append(referenceId).append("\n");
        teamEmailBody.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        teamEmailBody.append("Priority: HIGH - IMMEDIATE ACTION REQUIRED\n\n");
        
        teamEmailBody.append("CUSTOMER DETAILS:\n");
        if (userName != null && !userName.trim().isEmpty()) {
            teamEmailBody.append("Name: ").append(userName).append("\n");
        }
        teamEmailBody.append("Email: ").append(userEmail).append("\n");
        if (userPhone != null && !userPhone.trim().isEmpty()) {
            teamEmailBody.append("Phone: ").append(userPhone).append("\n");
        }
        teamEmailBody.append("\n");
        
        if (emergencyDetails != null && !emergencyDetails.trim().isEmpty()) {
            teamEmailBody.append("EMERGENCY DETAILS:\n").append(emergencyDetails).append("\n\n");
        }
        
        teamEmailBody.append("ACTION REQUIRED:\n");
        teamEmailBody.append("• Contact customer immediately at: ").append(userPhone != null ? userPhone : userEmail).append("\n");
        teamEmailBody.append("• Provide emergency assistance\n");
        teamEmailBody.append("• Update ticket status after resolution\n\n");
        
        teamEmailBody.append("Customer has been sent confirmation email with reference ID: ").append(referenceId);
        
        teamMessage.setText(teamEmailBody.toString());
        mailSender.send(teamMessage);
        
        return true;
    } catch (Exception e) {
        System.err.println("Team notification email failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    // private boolean sendSMS(String phoneNumber, String messageText) {
    //     try {
    //         OkHttpClient client = new OkHttpClient();
            
    //         RequestBody formBody = new FormBody.Builder()
    //                 .add("authorization", SMS_API_KEY)
    //                 .add("sender_id", "FTWSMS")
    //                 .add("message", messageText)
    //                 .add("language", "english")
    //                 .add("route", "p")
    //                 .add("numbers", phoneNumber.replace("+91", ""))
    //                 .build();

    //         Request request = new Request.Builder()
    //                 .url(SMS_API_URL)
    //                 .post(formBody)
    //                 .build();

    //         Response response = client.newCall(request).execute();
    //         return response.isSuccessful();
            
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    private String generateGoogleMeetLink() {
        // Generate a unique meeting room ID
        String meetingId = "machinery-emergency-" + System.currentTimeMillis();
        return "https://meet.google.com/" + meetingId;
    }

    public boolean sendSMSViaTextBelt(String phoneNumber, String messageText) {
    try {
        OkHttpClient client = new OkHttpClient();
        
        RequestBody formBody = new FormBody.Builder()
                .add("phone", phoneNumber)
                .add("message", messageText)
                .add("key", "textbelt") // Free tier - replace with paid key for production
                .build();

        Request request = new Request.Builder()
                .url("https://textbelt.com/text")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
        
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}


public boolean sendEmergencyEmailToAdmin(String userEmail, String userName, String userPhone, String emergencyDetails) {
    try {
        // Your fixed admin email where you want to receive emergency requests
        String adminEmail = "sharmaharsh472000@gmail.com";
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail); // Send TO your email
        message.setFrom(fromEmail);
        message.setSubject("🚨 NEW EMERGENCY REQUEST - Immediate Action Required");
        
        // Generate a unique reference ID for tracking
        String referenceId = "EMG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("🚨 EMERGENCY REQUEST RECEIVED 🚨\n\n");
        emailBody.append("===========================================\n");
        emailBody.append("Reference ID: ").append(referenceId).append("\n");
        emailBody.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        emailBody.append("Priority: HIGH - IMMEDIATE ACTION REQUIRED\n");
        emailBody.append("===========================================\n\n");
        
        emailBody.append("CUSTOMER DETAILS:\n");
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        if (userName != null && !userName.trim().isEmpty()) {
            emailBody.append("👤 Name: ").append(userName).append("\n");
        } else {
            emailBody.append("👤 Name: Not provided\n");
        }
        
        emailBody.append("📧 Email: ").append(userEmail).append("\n");
        
        if (userPhone != null && !userPhone.trim().isEmpty()) {
            emailBody.append("📱 Phone: ").append(userPhone).append("\n");
        } else {
            emailBody.append("📱 Phone: Not provided\n");
        }
        
        emailBody.append("\n");
        
        if (emergencyDetails != null && !emergencyDetails.trim().isEmpty()) {
            emailBody.append("EMERGENCY DETAILS:\n");
            emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            emailBody.append(emergencyDetails).append("\n\n");
        } else {
            emailBody.append("EMERGENCY DETAILS:\n");
            emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            emailBody.append("No specific details provided\n\n");
        }
        
        emailBody.append("IMMEDIATE ACTION REQUIRED:\n");
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("🔥 Contact customer immediately\n");
        if (userPhone != null && !userPhone.trim().isEmpty()) {
            emailBody.append("📞 Call: ").append(userPhone).append("\n");
        }
        emailBody.append("📧 Email: ").append(userEmail).append("\n");
        emailBody.append("⚡ Provide emergency assistance\n");
        emailBody.append("📋 Update ticket status after resolution\n\n");
        
        emailBody.append("EMERGENCY RESPONSE CHECKLIST:\n");
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("☐ Contact customer within 15 minutes\n");
        emailBody.append("☐ Assess emergency situation\n");
        emailBody.append("☐ Dispatch technical team if needed\n");
        emailBody.append("☐ Send follow-up confirmation to customer\n");
        emailBody.append("☐ Update emergency log with resolution\n\n");
        
        emailBody.append("This emergency request was submitted through the website.\n");
        emailBody.append("Reference ID: ").append(referenceId).append("\n");
        emailBody.append("Timestamp: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n\n");
        
        emailBody.append("--- Automated Emergency Alert System ---");
        
        message.setText(emailBody.toString());
        
        // Send emergency notification to your email
        mailSender.send(message);
        
        System.out.println("Emergency notification sent to admin: " + adminEmail);
        System.out.println("Reference ID: " + referenceId);
        
        return true;
    } catch (Exception e) {
        System.err.println("Emergency notification sending failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Optional: Send confirmation to user that their request was received
public boolean sendConfirmationToUser(String userEmail, String userName, String referenceId) {
    try {
        SimpleMailMessage confirmationMessage = new SimpleMailMessage();
        confirmationMessage.setTo(userEmail);
        confirmationMessage.setFrom(fromEmail);
        confirmationMessage.setSubject("✅ Emergency Request Received - Reference #" + referenceId);
        
        StringBuilder confirmationBody = new StringBuilder();
        confirmationBody.append("Emergency Request Confirmation\n\n");
        
        if (userName != null && !userName.trim().isEmpty()) {
            confirmationBody.append("Dear ").append(userName).append(",\n\n");
        } else {
            confirmationBody.append("Dear Customer,\n\n");
        }
        
        confirmationBody.append("Your emergency request has been received and our team has been notified immediately.\n\n");
        confirmationBody.append("Request Details:\n");
        confirmationBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        confirmationBody.append("Reference ID: ").append(referenceId).append("\n");
        confirmationBody.append("Time: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append("\n");
        confirmationBody.append("Status: Request received and being processed\n\n");
        
        confirmationBody.append("WHAT HAPPENS NEXT:\n");
        confirmationBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        confirmationBody.append("• Our emergency response team has been notified\n");
        confirmationBody.append("• You will receive a call within 15-30 minutes\n");
        confirmationBody.append("• Please keep your phone available\n");
        confirmationBody.append("• If life-threatening, please call emergency services\n\n");
        
        confirmationBody.append("Emergency Contact:\n");
        confirmationBody.append("Phone: +91 98244 62201\n");
        confirmationBody.append("Email: sharmaharsh472000@gmail.com\n\n");
        
        confirmationBody.append("Thank you for contacting us. Help is on the way!\n\n");
        confirmationBody.append("Best regards,\n");
        confirmationBody.append("Emergency Response Team\n");
        confirmationBody.append("Heavy Machinery Services");
        
        confirmationMessage.setText(confirmationBody.toString());
        mailSender.send(confirmationMessage);
        
        return true;
    } catch (Exception e) {
        System.err.println("User confirmation email failed: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}