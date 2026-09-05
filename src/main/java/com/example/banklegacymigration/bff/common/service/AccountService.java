package com.example.banklegacymigration.bff.common.service;

import com.example.banklegacymigration.bff.common.model.Account;
import com.example.banklegacymigration.bff.common.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccount(Long cuentaId) {
        return accountRepository.findById(cuentaId);
    }
}