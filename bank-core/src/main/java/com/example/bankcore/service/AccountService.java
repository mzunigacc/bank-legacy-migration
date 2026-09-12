package com.example.bankcore.service;

import com.example.bankcore.entity.Account;
import com.example.bankcore.entity.AccountMovement;
import com.example.bankcore.repository.AccountMovementRepository;
import com.example.bankcore.repository.AccountRepository;
import org.springframework.stereotype.Service;

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

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccount(Long cuentaId) {
        return accountRepository.findById(cuentaId);
    }

    public List<AccountMovement> getMovements(Long cuentaId) {
        return accountMovementRepository
                .findByCuentaIdOrderByFechaDesc(cuentaId);
    }
}