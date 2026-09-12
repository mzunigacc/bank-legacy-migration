package com.example.banklegacymigration.bff.atm.service;

import com.example.banklegacymigration.bff.atm.dto.AtmBalanceResponse;
import com.example.banklegacymigration.bff.atm.dto.WithdrawalResponse;
import com.example.banklegacymigration.bff.atm.exception.AccountNotFoundException;
import com.example.banklegacymigration.bff.atm.exception.InsufficientFundsException;
import com.example.banklegacymigration.bff.atm.exception.InvalidWithdrawalAmountException;
import com.example.banklegacymigration.bff.common.model.Account;
import com.example.banklegacymigration.bff.common.repository.AccountRepository;
import com.example.banklegacymigration.bff.common.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AtmAccountService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public AtmAccountService(
            AccountService accountService,
            AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    public Optional<AtmBalanceResponse> getBalance(Long cuentaId) {

        Optional<Account> account = accountService.getAccount(cuentaId);

        return account.map(value ->
                new AtmBalanceResponse(
                        value.getCuentaId(),
                        value.getSaldoFinal()
                )
        );
    }

    @Transactional
    public WithdrawalResponse withdraw(
            Long cuentaId,
            BigDecimal monto) {

        Account account = accountService.getAccount(cuentaId)
                .orElseThrow(() ->
                        new AccountNotFoundException(cuentaId)
                );

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWithdrawalAmountException();
        }

        BigDecimal saldoActual = account.getSaldoFinal();

        if (monto.compareTo(saldoActual) > 0) {
            throw new InsufficientFundsException();
        }

        BigDecimal nuevoSaldo = saldoActual.subtract(monto);

        accountRepository.updateBalance(
                cuentaId,
                nuevoSaldo
        );

        accountRepository.saveWithdrawal(
                cuentaId,
                monto
        );

        return new WithdrawalResponse(
                cuentaId,
                monto,
                nuevoSaldo,
                "APROBADO"
        );
    }
}