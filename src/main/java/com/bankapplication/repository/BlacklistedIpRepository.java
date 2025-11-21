package com.bankapplication.repository;

import com.bankapplication.model.BlacklistedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedIpRepository extends JpaRepository<BlacklistedIp, Long> {
    boolean existsByIp(String ip);
}