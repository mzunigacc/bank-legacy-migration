package com.example.bankcore.service;

import com.example.bankcore.dto.WithdrawalResult;
import com.example.bankcore.entity.Account;
import com.example.bankcore.entity.AtmWithdrawal;
import com.example.bankcore.event.WithdrawalCreatedEvent;
import com.example.bankcore.exception.AccountNotFoundException;
import com.example.bankcore.exception.InsufficientFundsException;
import com.example.bankcore.exception.InvalidWithdrawalAmountException;
import com.example.bankcore.messaging.WithdrawalEventProducer;
import com.example.bankcore.repository.AccountRepository;
import com.example.bankcore.repository.AtmWithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WithdrawalService {

    private final AccountRepository accountRepository;
    private final AtmWithdrawalRepository atmWithdrawalRepository;
    private final WithdrawalEventProducer withdrawalEventProducer;

    public WithdrawalService(
            AccountRepository accountRepository,
            AtmWithdrawalRepository atmWithdrawalRepository,
            WithdrawalEventProducer withdrawalEventProducer) {

        this.accountRepository = accountRepository;
        this.atmWithdrawalRepository = atmWithdrawalRepository;
        this.withdrawalEventProducer = withdrawalEventProducer;
    }

    @Transactional
    public WithdrawalResult withdraw(Long cuentaId, BigDecimal monto) {

        Account account = accountRepository.findById(cuentaId)
                .orElseThrow(() -> new AccountNotFoundException(cuentaId));

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWithdrawalAmountException();
        }

        BigDecimal saldoAnterior = account.getSaldoFinal();

        if (monto.compareTo(saldoAnterior) > 0) {
            throw new InsufficientFundsException();
        }

        BigDecimal saldoNuevo = saldoAnterior.subtract(monto);

        account.setSaldoFinal(saldoNuevo);
        accountRepository.save(account);

        AtmWithdrawal withdrawal = new AtmWithdrawal(
                cuentaId,
                LocalDateTime.now(),
                monto
        );

        AtmWithdrawal savedWithdrawal =
                atmWithdrawalRepository.save(withdrawal);

        WithdrawalCreatedEvent event = new WithdrawalCreatedEvent(
                savedWithdrawal.getId(),
                cuentaId,
                monto,
                saldoAnterior,
                saldoNuevo,
                savedWithdrawal.getFechaHora()
        );

        withdrawalEventProducer.publish(event);

        return new WithdrawalResult(
                cuentaId,
                monto,
                saldoAnterior,
                saldoNuevo
        );
    }
}