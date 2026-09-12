package com.example.bankcore.repository;

import com.example.bankcore.entity.AtmWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmWithdrawalRepository
        extends JpaRepository<AtmWithdrawal, Long> {
}