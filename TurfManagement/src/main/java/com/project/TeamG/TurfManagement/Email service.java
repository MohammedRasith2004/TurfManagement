package com.project.TeamG.TurfManagement;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    public static void sendEmail(String toEmail, String subject, String messageText) {
        final String fromEmail = "youremail@gmail.com";  // 🔒 Replace with your Gmail
        final String password = "your_app_password";     // 🔒 Use Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            System.out.println("📧 Email sent successfully to admin.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}