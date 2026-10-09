package com.example.accountservice.service;

import com.example.accountservice.dto.DebitResult;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.AccountMovement;
import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.exception.InsufficientFundsException;
import com.example.accountservice.repository.AccountMovementRepository;
import com.example.accountservice.repository.AccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMovementRepository accountMovementRepository;

    public AccountService(
            AccountRepository accountRepository,
            AccountMovementRepository accountMovementRepository) {

        this.accountRepository = accountRepository;
        this.accountMovementRepository = accountMovementRepository;
    }

    public Optional<Account> getAccount(Long cuentaId) {
        return accountRepository.findById(cuentaId);
    }

    public List<AccountMovement> getMovements(Long cuentaId) {
        return accountMovementRepository
                .findByCuentaIdOrderByFechaDesc(cuentaId);
    }

    @Transactional
    public DebitResult debit(Long cuentaId, BigDecimal monto) {

        Account account = accountRepository.findById(cuentaId)
                .orElseThrow(() -> new AccountNotFoundException(cuentaId));

        BigDecimal saldoAnterior = account.getSaldoFinal();

        if (monto.compareTo(saldoAnterior) > 0) {
            throw new InsufficientFundsException();
        }

        BigDecimal saldoNuevo = saldoAnterior.subtract(monto);

        account.setSaldoFinal(saldoNuevo);
        accountRepository.save(account);

        return new DebitResult(
                cuentaId,
                monto,
                saldoAnterior,
                saldoNuevo
        );
    }
}
