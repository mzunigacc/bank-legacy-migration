package com.example.banklegacymigration.bff.common.repository;

import com.example.banklegacymigration.bff.common.entity.AtmWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtmWithdrawalRepository
        extends JpaRepository<AtmWithdrawal, Long> {
}