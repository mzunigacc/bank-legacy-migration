package com.example.bffmobile.service;

import com.example.bffmobile.client.AccountServiceClient;
import com.example.bffmobile.client.dto.CoreAccountResponse;
import com.example.bffmobile.client.dto.CoreMovementResponse;
import com.example.bffmobile.dto.MobileAccountDetailResponse;
import com.example.bffmobile.dto.MobileMovementResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MobileAccountService {

    private static final int MOVEMENT_LIMIT = 2;

    private final AccountServiceClient accountServiceClient;

    public MobileAccountService(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    public Optional<MobileAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<CoreAccountResponse> account =
                accountServiceClient.getAccount(cuentaId);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        List<CoreMovementResponse> movements =
                accountServiceClient.getMovements(cuentaId);

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