package org.bisag.jkcip.controllers;

import java.util.HashMap;
import java.util.Map;

import org.bisag.jkcip.dto.request.RentRequest;
import org.bisag.jkcip.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
// @CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "https://mamtaengineering.vercel.app")

public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello from ContactController!");
    }

    @PostMapping("/service-request")
public ResponseEntity<Map<String, String>> requestService() {
    Map<String, String> response = new HashMap<>();
    
    try {
        System.out.println("Received service request");
        
        boolean smsSent = contactService.sendSMSWithHeader("","");
        
        if (smsSent) {
            response.put("status", "success");
            response.put("message", "Service request SMS sent successfully!");
            System.out.println("SMS sent successfully");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to send SMS - check server logs for details");
            System.err.println("SMS sending returned false");
        }
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        System.err.println("Controller exception: " + e.getMessage());
        e.printStackTrace();
        response.put("status", "error");
        response.put("message", "Server error: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}

    // @PostMapping("/emergency-email")
    // public ResponseEntity<Map<String, String>> sendEmergencyEmail() {
    //     Map<String, String> response = new HashMap<>();
        
    //     try {
    //         boolean emailSent = contactService.sendEmergencyEmail();
            
    //         if (emailSent) {
    //             response.put("status", "success");
    //             response.put("message", "Emergency email sent successfully!");
    //         } else {
    //             response.put("status", "error");
    //             response.put("message", "Failed to send email");
    //         }
            
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         response.put("status", "error");
    //         response.put("message", "Error: " + e.getMessage());
    //         return ResponseEntity.status(500).body(response);
    //     }
    // }

    @PostMapping("/emergency-email")
public ResponseEntity<Map<String, String>> sendEmergencyEmail(@RequestBody Map<String, String> requestData) {
    Map<String, String> response = new HashMap<>();
    
    try {
        String userEmail = requestData.get("email");
        String userName = requestData.get("name");
        String userPhone = requestData.get("phoneNumber");
        String emergencyDetails = requestData.get("details");
        
        // Validate email
        if (userEmail == null || userEmail.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email address is required");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Basic email validation
        if (!isValidEmail(userEmail)) {
            response.put("status", "error");
            response.put("message", "Invalid email format");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Send emergency details to your admin email
        boolean emailSentToAdmin = contactService.sendEmergencyEmailToAdmin(userEmail, userName, userPhone, emergencyDetails);
        
        if (emailSentToAdmin) {
            // Optionally, also send confirmation to user
            String referenceId = "EMG-" + System.currentTimeMillis();
            contactService.sendConfirmationToUser(userEmail, userName, referenceId);
            
            response.put("status", "success");
            response.put("message", "Emergency request submitted successfully! Our team will contact you shortly.");
        } else {
            response.put("status", "error");
            response.put("message", "Failed to submit emergency request");
        }
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        System.err.println("Emergency request error: " + e.getMessage());
        e.printStackTrace();
        response.put("status", "error");
        response.put("message", "Server error: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}

private boolean isValidEmail(String email) {
    return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
}


 @PostMapping("/rent-request")
    public ResponseEntity<Map<String, String>> submitRentRequest(@RequestBody RentRequest rentRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            // Basic validation
            if (rentRequest.getName() == null || rentRequest.getName().trim().isEmpty()
                || rentRequest.getEmail() == null || rentRequest.getEmail().trim().isEmpty()
                || rentRequest.getMobile() == null || rentRequest.getMobile().trim().isEmpty()
                || rentRequest.getState() == null || rentRequest.getState().trim().isEmpty()
                || rentRequest.getDistrict() == null || rentRequest.getDistrict().trim().isEmpty()
                || rentRequest.getVillage() == null || rentRequest.getVillage().trim().isEmpty()
                || rentRequest.getRentTime() == null || rentRequest.getRentTime().trim().isEmpty()
                || rentRequest.getEquipmentType() == null || rentRequest.getEquipmentType().trim().isEmpty()) {
                response.put("status", "error");
                response.put("message", "All fields are required");
                return ResponseEntity.badRequest().body(response);
            }

            // Email format check
            if (!rentRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                response.put("status", "error");
                response.put("message", "Invalid email format");
                return ResponseEntity.badRequest().body(response);
            }

            boolean sent = contactService.handleRentRequestEmails(rentRequest);

            if (sent) {
                response.put("status", "success");
                response.put("message", "Your request has been submitted successfully! We will contact you immediately.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Failed to send emails. Please try again later.");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            System.err.println("submitRentRequest error: " + e.getMessage());
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}