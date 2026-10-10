package com.example.paymentservice.service;

import com.example.paymentservice.client.AccountServiceClient;
import com.example.paymentservice.dto.PaymentResult;
import com.example.paymentservice.entity.PaymentOperation;
import com.example.paymentservice.event.PaymentProcessedEvent;
import com.example.paymentservice.messaging.PaymentEventProducer;
import com.example.paymentservice.repository.PaymentOperationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final AccountServiceClient accountServiceClient;
    private final PaymentOperationRepository operationRepository;
    private final PaymentEventProducer eventProducer;

    public PaymentService(
            AccountServiceClient accountServiceClient,
            PaymentOperationRepository operationRepository,
            PaymentEventProducer eventProducer) {

        this.accountServiceClient = accountServiceClient;
        this.operationRepository = operationRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public PaymentResult deposit(
            Long cuentaDestinoId,
            BigDecimal monto,
            String bearerToken) {

        accountServiceClient.credit(
                cuentaDestinoId,
                monto,
                bearerToken
        );

        return persistAndPublish(
                "DEPOSIT",
                null,
                cuentaDestinoId,
                monto
        );
    }

    @Transactional
    public PaymentResult transfer(
            Long cuentaOrigenId,
            Long cuentaDestinoId,
            BigDecimal monto,
            String bearerToken) {

        accountServiceClient.transfer(
                cuentaOrigenId,
                cuentaDestinoId,
                monto,
                bearerToken
        );

        return persistAndPublish(
                "TRANSFER",
                cuentaOrigenId,
                cuentaDestinoId,
                monto
        );
    }

    private PaymentResult persistAndPublish(
            String operationType,
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal amount) {

        LocalDateTime createdAt = LocalDateTime.now();

        PaymentOperation operation =
                operationRepository.save(
                        new PaymentOperation(
                                operationType,
                                sourceAccountId,
                                targetAccountId,
                                amount,
                                createdAt
                        )
                );

        PaymentProcessedEvent event =
                new PaymentProcessedEvent(
                        operation.getId(),
                        operationType,
                        sourceAccountId,
                        targetAccountId,
                        amount,
                        createdAt
                );

        eventProducer.publish(event);

        return new PaymentResult(
                operation.getId(),
                operationType,
                sourceAccountId,
                targetAccountId,
                amount,
                createdAt
        );
    }
}
