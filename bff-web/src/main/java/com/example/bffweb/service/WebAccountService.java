package com.example.bffweb.service;

import com.example.bffweb.client.BankCoreClient;
import com.example.bffweb.client.dto.CoreAccountResponse;
import com.example.bffweb.client.dto.CoreMovementResponse;
import com.example.bffweb.dto.WebAccountDetailResponse;
import com.example.bffweb.dto.WebMovementResponse;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebAccountService {

    private final BankCoreClient bankCoreClient;

    public WebAccountService(BankCoreClient bankCoreClient) {
        this.bankCoreClient = bankCoreClient;
    }

    public Optional<WebAccountDetailResponse> getAccount(Long cuentaId) {

        Optional<CoreAccountResponse> account =
                bankCoreClient.getAccount(cuentaId);

        if (account.isEmpty()) {
            return Optional.empty();
        }

        List<CoreMovementResponse> movements =
                bankCoreClient.getMovements(cuentaId);

        return Optional.of(
                toDetailResponse(account.get(), movements)
        );
    }

    private WebAccountDetailResponse toDetailResponse(
            CoreAccountResponse account,
            List<CoreMovementResponse> movements) {

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