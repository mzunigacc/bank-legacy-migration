package com.example.banklegacymigration.bff.web.service;

import com.example.banklegacymigration.bff.common.entity.Account;
import com.example.banklegacymigration.bff.common.entity.AccountMovement;
import com.example.banklegacymigration.bff.common.service.AccountService;
import com.example.banklegacymigration.bff.web.dto.WebAccountDetailResponse;
import com.example.banklegacymigration.bff.web.dto.WebMovementResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebAccountService {

    private final AccountService accountService;

    public WebAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<WebAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<Account> account = accountService.getAccount(cuentaId);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        List<AccountMovement> movements =
                accountService.getMovements(cuentaId);

        return Optional.of(
                toDetailResponse(account.get(), movements)
        );
    }

    private WebAccountDetailResponse toDetailResponse(
            Account account,
            List<AccountMovement> movements) {

        List<WebMovementResponse> movementResponses = movements.stream()
                .map(movement -> new WebMovementResponse(
                        movement.getFecha(),
                        movement.getTransaccion(),
                        movement.getMonto(),
                        movement.getDescripcion()
                ))
                .toList();

        return new WebAccountDetailResponse(
                account.getCuentaId(),
                account.getNombre(),
                account.getEdad(),
                account.getTipo(),
                account.getSaldoFinal(),
                account.getInteres(),
                movementResponses
        );
    }
}