package com.example.banklegacymigration.bff.mobile.service;

import com.example.banklegacymigration.bff.common.model.Account;
import com.example.banklegacymigration.bff.common.model.AccountMovement;
import com.example.banklegacymigration.bff.common.service.AccountService;
import com.example.banklegacymigration.bff.mobile.dto.MobileAccountDetailResponse;
import com.example.banklegacymigration.bff.mobile.dto.MobileMovementResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MobileAccountService {

    private static final int MOVEMENT_LIMIT = 2;

    private final AccountService accountService;

    public MobileAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<MobileAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<Account> account = accountService.getAccount(cuentaId);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        List<AccountMovement> movements =
                accountService.getMovements(cuentaId);

        List<MobileMovementResponse> recentMovements = movements.stream()
                .limit(MOVEMENT_LIMIT)
                .map(movement -> new MobileMovementResponse(
                        movement.getFecha(),
                        movement.getMonto()
                ))
                .toList();

        return Optional.of(
                new MobileAccountDetailResponse(
                        account.get().getCuentaId(),
                        account.get().getSaldoFinal(),
                        recentMovements
                )
        );
    }
}