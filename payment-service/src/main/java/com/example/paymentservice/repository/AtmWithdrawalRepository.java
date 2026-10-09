package com.example.paymentservice.repository;

import com.example.paymentservice.entity.AtmWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtmWithdrawalRepository extends JpaRepository<AtmWithdrawal, Long> {
}
