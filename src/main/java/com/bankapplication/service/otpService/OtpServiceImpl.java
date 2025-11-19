package com.bankapplication.service.otpService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveOtp(String key, String otp) {
        redisTemplate.opsForValue().set(key, otp, 5, TimeUnit.MINUTES);
    }

    @Override
    public boolean verifyOtp(String key, String otp) {
        String cachedOtp = redisTemplate.opsForValue().get(key);
        return cachedOtp != null && cachedOtp.equals(otp);
    }

    @Override
    public void invalidateOtp(String key) {
        redisTemplate.delete(key);
    }
}
