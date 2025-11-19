package com.bankapplication.util;

import java.util.Random;

public class OtpUtil {

        public static String generateOtp(int length) {
            String digits = "0123456789";
            Random rnd = new Random();
            StringBuilder otp = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                otp.append(digits.charAt(rnd.nextInt(digits.length())));
            }
            return otp.toString();
        }
    }


