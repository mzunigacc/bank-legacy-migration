package com.example.bffmobile.service;

import com.example.bffmobile.client.BankCoreClient;
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

    private final BankCoreClient bankCoreClient;

    public MobileAccountService(BankCoreClient bankCoreClient) {
        this.bankCoreClient = bankCoreClient;
    }

    public Optional<MobileAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<CoreAccountResponse> account =
                bankCoreClient.getAccount(cuentaId);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        List<CoreMovementResponse> movements =
                bankCoreClient.getMovements(cuentaId);

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