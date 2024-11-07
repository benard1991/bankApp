package com.bankApp.util;

import java.util.Random;

public class AccountNumberGenerator {

    public static String generateAccountNumber() {
        Random random = new Random();
        // Generate a random 10-digit number
        long accountNumber = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        return Long.toString(accountNumber);
    }
}
