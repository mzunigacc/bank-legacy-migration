package com.example.paymentservice.repository;

import com.example.paymentservice.entity.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOperationRepository
        extends JpaRepository<PaymentOperation, Long> {
}
