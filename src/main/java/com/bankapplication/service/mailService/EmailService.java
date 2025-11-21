package com.bankapplication.service.mailService;

import java.math.BigDecimal;

public interface EmailService {

    void sendResetPasswordLink(String toEmail, String token, String firstName);
    void sendRegistrationEmail(String toEmail, String firstName);
    void sendTransactionNotification(String toEmail, String firstName, String type, double amount, double balance);
    void sendLoginNotification(String toEmail, String firstName);
    void sendAccountDeactivatedEmail(String toEmail, String firstName);
    void sendAccountActivatedEmail(String toEmail, String firstName);
    void sendPasswordChangeConfirmation(String toEmail, String firstName);

    public void sendOtpEmail(String firstName, String username, String otp);
    public void sendDepositLimitAlert(String toEmail, String firstName, BigDecimal depositAmount, BigDecimal limit);

    }