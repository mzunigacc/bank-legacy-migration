package com.example.bffatm.service;

import com.example.bffatm.client.AccountServiceClient;
import com.example.bffatm.client.PaymentServiceClient;
import com.example.bffatm.client.dto.CoreAccountResponse;
import com.example.bffatm.client.dto.CoreWithdrawalResponse;
import com.example.bffatm.dto.AtmBalanceResponse;
import com.example.bffatm.dto.WithdrawalResponse;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AtmAccountService {

    private final AccountServiceClient accountServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public AtmAccountService(
            AccountServiceClient accountServiceClient,
            PaymentServiceClient paymentServiceClient) {

        this.accountServiceClient = accountServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public Optional<AtmBalanceResponse> getBalance(Long cuentaId) {

        Optional<CoreAccountResponse> account =
                accountServiceClient.getAccount(cuentaId);

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

        CoreWithdrawalResponse paymentResponse =
                paymentServiceClient.withdraw(cuentaId, monto);

        return new WithdrawalResponse(
                paymentResponse.getCuentaId(),
                paymentResponse.getMonto(),
                paymentResponse.getSaldoNuevo(),
                "APROBADO"
        );
    }
}
