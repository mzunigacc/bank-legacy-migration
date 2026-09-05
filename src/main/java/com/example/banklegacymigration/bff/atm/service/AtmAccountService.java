package com.example.banklegacymigration.bff.atm.service;

import com.example.banklegacymigration.bff.atm.dto.AtmBalanceResponse;
import com.example.banklegacymigration.bff.common.model.Account;
import com.example.banklegacymigration.bff.common.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtmAccountService {

    private final AccountService accountService;

    public AtmAccountService(AccountService accountService) {
        this.accountService = accountService;
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
}