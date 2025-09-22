package com.bankapplication.repository;

import com.bankapplication.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate >= :start AND t.transactionDate < :end")
    int countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transferChannel = :type")
    Double sumByType(@Param("type") String type);

    @Query(value = "SELECT status, COUNT(*) FROM transactions WHERE DATE(transaction_date) = CURDATE() GROUP BY status", nativeQuery = true)
    List<Object[]> getTodayTransactionStats();

}
