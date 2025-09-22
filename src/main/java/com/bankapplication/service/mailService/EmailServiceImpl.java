package com.bankapplication.service.mailService;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendResetPasswordLink(String toEmail, String token, String firstName) {
        String resetUrl = "https://yourdomain.com/reset-password?token=" + token;

        String body = """
                Hello, %s

                We received a request to reset your password.
                Click the link below to reset it:
                %s

                If you didn’t request this, ignore this email.

                Best regards,
                Bank App Team
                """.formatted(firstName, resetUrl);

        sendEmail(toEmail, "Password Reset Request", body);
    }

    @Async
    public void sendRegistrationEmail(String toEmail, String firstName) {
        String body = """
                Welcome, %s!

                Your account has been successfully created.
                You can now log in and start using our services.

                Thanks for choosing us,
                Bank App Team
                """.formatted(firstName);

        sendEmail(toEmail, "Account Registration Successful", body);
    }

    @Async
    public void sendTransactionNotification(String toEmail, String firstName, String type, double amount, double balance) {
        String body = """
                Hello, %s

                A recent %s transaction occurred on your account.

                Amount: $%.2f
                Current Balance: $%.2f

                If you did not authorize this, please contact support immediately.

                Best regards,
                Bank App Team
                """.formatted(firstName, type, amount, balance);

        sendEmail(toEmail, "Transaction Alert - " + type, body);
    }

    @Async
    public void sendLoginNotification(String toEmail, String firstName) {
        String body = """
                Hello, %s

                A login to your account was just detected.

                If this was not you, please reset your password immediately.

                Best regards,
                Bank App Team
                """.formatted(firstName);

        sendEmail(toEmail, "New Login Detected", body);
    }


    @Async
    public void sendAccountActivatedEmail(String toEmail, String firstName) {
        String body = """
                Hello %s,

                Your account has been successfully activated.
                You can now log in and access all features.

                If you didn’t request this, please contact support immediately.

                Best regards,
                Bank App Team
                """.formatted(firstName);

        sendEmail(toEmail, "Account Activated", body);
    }

    @Async
    public void sendAccountDeactivatedEmail(String toEmail, String firstName) {
        String body = """
                Hello %s,

                Your account has been deactivated and you will not be able to log in.

                If this was a mistake or you have any concerns, please contact our support team.

                Best regards,
                Bank App Team
                """.formatted(firstName);

        sendEmail(toEmail, "Account Deactivated", body);
    }

    @Async
    public void sendPasswordChangeConfirmation(String toEmail, String firstName) {
        String body = """
            Hello %s,

            Your password has been successfully changed.

            If you did not make this change, please contact our support team immediately or reset your password.

            Stay secure,
            Bank App Team
            """.formatted(firstName);

        sendEmail(toEmail, "Password Changed Successfully", body);
    }
}
