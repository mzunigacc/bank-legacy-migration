package com.example.paymentservice.service;

import com.example.paymentservice.client.AccountServiceClient;
import com.example.paymentservice.dto.DebitResult;
import com.example.paymentservice.dto.WithdrawalResult;
import com.example.paymentservice.entity.AtmWithdrawal;
import com.example.paymentservice.event.WithdrawalCreatedEvent;
import com.example.paymentservice.messaging.WithdrawalEventProducer;
import com.example.paymentservice.repository.AtmWithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WithdrawalService {

    private final AccountServiceClient accountServiceClient;
    private final AtmWithdrawalRepository withdrawalRepository;
    private final WithdrawalEventProducer eventProducer;

    public WithdrawalService(
            AccountServiceClient accountServiceClient,
            AtmWithdrawalRepository withdrawalRepository,
            WithdrawalEventProducer eventProducer
    ) {
        this.accountServiceClient = accountServiceClient;
        this.withdrawalRepository = withdrawalRepository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public WithdrawalResult withdraw(
            Long cuentaId,
            BigDecimal monto,
            String bearerToken
    ) {
        DebitResult debit = accountServiceClient.debit(
                cuentaId,
                monto,
                bearerToken
        );

        LocalDateTime fechaHora = LocalDateTime.now();

        AtmWithdrawal withdrawal = withdrawalRepository.save(
                new AtmWithdrawal(cuentaId, fechaHora, monto)
        );

        WithdrawalCreatedEvent event = new WithdrawalCreatedEvent(
                withdrawal.getId(),
                cuentaId,
                monto,
                debit.saldoAnterior(),
                debit.saldoNuevo(),
                fechaHora
        );

        eventProducer.publish(event);

        return new WithdrawalResult(
                withdrawal.getId(),
                cuentaId,
                monto,
                debit.saldoAnterior(),
                debit.saldoNuevo()
        );
    }
}
