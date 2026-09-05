package com.example.banklegacymigration.bff.web.service;

import com.example.banklegacymigration.bff.common.model.Account;
import com.example.banklegacymigration.bff.common.service.AccountService;
import com.example.banklegacymigration.bff.web.dto.WebAccountDetailResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebAccountService {

    private final AccountService accountService;

    public WebAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<WebAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<Account> account = accountService.getAccount(cuentaId);

        return account.map(this::toDetailResponse);
    }

    private WebAccountDetailResponse toDetailResponse(Account account) {
        return new WebAccountDetailResponse(
                account.getCuentaId(),
                account.getNombre(),
                account.getEdad(),
                account.getTipo(),
                account.getSaldoFinal(),
                account.getInteres()
        );
    }
}