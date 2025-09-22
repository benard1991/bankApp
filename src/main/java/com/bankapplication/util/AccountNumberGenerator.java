package com.bankapplication.util;

import java.util.Random;

public class AccountNumberGenerator {
    public static String generateAccountNumber() {
        Random random = new Random();
        long number = 1_000_000_000L + (long)(random.nextDouble() * 9_000_000_000L);
        return String.valueOf(number);
    }
}
