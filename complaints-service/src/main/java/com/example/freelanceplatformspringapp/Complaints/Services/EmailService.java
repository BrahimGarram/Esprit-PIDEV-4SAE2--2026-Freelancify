package com.example.freelanceplatformspringapp.Complaints.Services;

import com.example.freelanceplatformspringapp.Complaints.Entity.Complaints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@freelancity.com}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:4200}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        log.info("EmailService initialized successfully");
        log.info("Email will be sent from: {}", fromEmail);
        log.info("Base URL: {}", baseUrl);
    }

    /**
     * Send email notification when complaint status changes
     */
    public void sendStatusChangeNotification(String userEmail, Complaints complaint, String previousStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Complaint Status Updated - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear User,\n\n" +
                "Your complaint (ID: %d) status has been updated.\n\n" +
                "Title: %s\n" +
                "Previous Status: %s\n" +
                "New Status: %s\n\n" +
                "View your complaint: %s/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity Support Team",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                previousStatus,
                complaint.getClaimStatus(),
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Status change notification sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send status change notification to: {}", userEmail, e);
        }
    }

    /**
     * Send email notification when new complaint is created
     */
    public void sendComplaintCreatedNotification(String userEmail, Complaints complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Complaint Received - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear User,\n\n" +
                "Thank you for submitting your complaint. We have received it and our team will review it shortly.\n\n" +
                "Complaint Details:\n" +
                "ID: %d\n" +
                "Title: %s\n" +
                "Description: %s\n" +
                "Priority: %s\n" +
                "Status: %s\n" +
                "Category: %s\n\n" +
                "You will receive email notifications when there are updates to your complaint.\n\n" +
                "View your complaint: %s/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity Support Team",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                complaint.getDescription(),
                complaint.getClaimPriority(),
                complaint.getClaimStatus(),
                complaint.getCategory() != null ? complaint.getCategory() : "Not categorized",
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Complaint creation notification sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send complaint creation notification to: {}", userEmail, e);
        }
    }

    /**
     * Send email notification when admin adds resolution note
     */
    public void sendResolutionNoteNotification(String userEmail, Complaints complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Admin Response to Your Complaint - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear User,\n\n" +
                "An admin has responded to your complaint (ID: %d).\n\n" +
                "Title: %s\n" +
                "Status: %s\n" +
                "Admin Response:\n%s\n\n" +
                "View your complaint: %s/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity Support Team",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                complaint.getClaimStatus(),
                complaint.getResolutionNote(),
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Resolution note notification sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send resolution note notification to: {}", userEmail, e);
        }
    }

    /**
     * Send reminder email for pending complaints
     */
    public void sendPendingComplaintReminder(String userEmail, Complaints complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Reminder: Your Complaint is Pending - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear User,\n\n" +
                "This is a reminder that your complaint (ID: %d) is still pending.\n\n" +
                "Title: %s\n" +
                "Status: %s\n" +
                "Created: %s\n\n" +
                "Our team is working on it. We'll update you soon.\n\n" +
                "View your complaint: %s/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity Support Team",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                complaint.getClaimStatus(),
                complaint.getCreatedAt(),
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Pending complaint reminder sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send pending complaint reminder to: {}", userEmail, e);
        }
    }

    /**
     * Send auto-close notification
     */
    public void sendAutoCloseNotification(String userEmail, Complaints complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject("Complaint Auto-Closed - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear User,\n\n" +
                "Your complaint (ID: %d) has been automatically closed due to inactivity.\n\n" +
                "Title: %s\n" +
                "Status: Closed\n\n" +
                "If you still need assistance, please create a new complaint.\n\n" +
                "View your complaints: %s/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity Support Team",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Auto-close notification sent to: {}", userEmail);
        } catch (Exception e) {
            log.error("Failed to send auto-close notification to: {}", userEmail, e);
        }
    }

    /**
     * Send auto-assignment notification to admin
     */
    public void sendAssignmentNotification(String adminEmail, Complaints complaint) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("New Complaint Assigned - #" + complaint.getIdReclamation());
            
            String body = String.format(
                "Dear Admin,\n\n" +
                "A new complaint has been assigned to you.\n\n" +
                "Complaint ID: %d\n" +
                "Title: %s\n" +
                "Category: %s\n" +
                "Priority: %s\n" +
                "Description: %s\n\n" +
                "View complaint: %s/admin/complaints\n\n" +
                "Best regards,\n" +
                "Freelancity System",
                complaint.getIdReclamation(),
                complaint.getTitle(),
                complaint.getCategory(),
                complaint.getClaimPriority(),
                complaint.getDescription(),
                baseUrl
            );
            
            message.setText(body);
            mailSender.send(message);
            log.info("Assignment notification sent to admin: {}", adminEmail);
        } catch (Exception e) {
            log.error("Failed to send assignment notification to admin: {}", adminEmail, e);
        }
    }
}
