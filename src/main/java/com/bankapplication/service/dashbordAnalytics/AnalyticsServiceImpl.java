package com.bankapplication.service.dashbordAnalytics;

import com.bankapplication.repository.TransactionRepository;
import com.bankapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Map<String, Object> getDashboardData() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return Map.of(
                "totalUsers", userRepository.count(),
                "newUsersThisMonth", userRepository.countUsersByDateRange(startOfMonth, endOfMonth),
                "totalDeposits", transactionRepository.sumByType("DEPOSIT"),
                "totalWithdrawals", transactionRepository.sumByType("WITHDRAWAL"),
                "todayTransactions", transactionRepository.countByDateRange(startOfDay, endOfDay),
                "todayTransactionBreakdown", getTodayTransactionBreakdown()
        );
    }

    private Map<String, Long> getTodayTransactionBreakdown() {
        return transactionRepository.getTodayTransactionStats()
                .stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]).toLowerCase().trim(),
                        row -> ((Number) row[1]).longValue()
                ));
    }
}
