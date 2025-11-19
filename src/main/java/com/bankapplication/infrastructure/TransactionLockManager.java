package com.bankapplication.infrastructure;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransactionLockManager {

    private final ConcurrentHashMap<String , Object> lock = new ConcurrentHashMap<>();

    public Object getLock(String accountNumber) {
        return lock.computeIfAbsent( accountNumber, K-> new Object());
    }
}
