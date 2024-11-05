package com.bankApp.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AccountNumberGenerator {

    public static long generateAccountNumber() {
        // Generate a random 10-digit number as long
        return ThreadLocalRandom.current().nextLong(1000000000L, 10000000000L);
    }
}
