package com.bankapplication.service.otpService;

public interface OtpService {

    public void saveOtp(String key, String otp) ;
    public boolean verifyOtp(String key, String otp);
    public void invalidateOtp(String key);
}
