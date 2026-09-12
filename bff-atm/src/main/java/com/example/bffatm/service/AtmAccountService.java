package com.example.bffatm.service;

import com.example.bffatm.client.BankCoreClient;
import com.example.bffatm.client.dto.CoreAccountResponse;
import com.example.bffatm.client.dto.CoreWithdrawalResponse;
import com.example.bffatm.dto.AtmBalanceResponse;
import com.example.bffatm.dto.WithdrawalResponse;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AtmAccountService {

    private final BankCoreClient bankCoreClient;

    public AtmAccountService(BankCoreClient bankCoreClient) {
        this.bankCoreClient = bankCoreClient;
    }

    public Optional<AtmBalanceResponse> getBalance(Long cuentaId) {

        Optional<CoreAccountResponse> account =
                bankCoreClient.getAccount(cuentaId);

        return account.map(value ->
                new AtmBalanceResponse(
                        value.getCuentaId(),
                        value.getSaldoFinal()
                )
        );
    }

    public WithdrawalResponse withdraw(
            Long cuentaId,
            BigDecimal monto) {

        CoreWithdrawalResponse coreResponse =
                bankCoreClient.withdraw(cuentaId, monto);

        return new WithdrawalResponse(
                coreResponse.getCuentaId(),
                coreResponse.getMonto(),
                coreResponse.getSaldoNuevo(),
                "APROBADO"
        );
    }
}