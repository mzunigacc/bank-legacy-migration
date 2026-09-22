package com.example.bffatm.client;

import com.example.bffatm.client.dto.CoreAccountResponse;
import com.example.bffatm.client.dto.CoreWithdrawalRequest;
import com.example.bffatm.client.dto.CoreWithdrawalResponse;
import com.example.bffatm.exception.CoreApiException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class BankCoreClient {

    private final RestClient restClient;

    public BankCoreClient(
            RestClient.Builder restClientBuilder,
            @Value("${bank.core.base-url}") String bankCoreBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(bankCoreBaseUrl)
                .build();
    }

    @CircuitBreaker(
            name = "bankCore",
            fallbackMethod = "getAccountFallback"
    )
    public Optional<CoreAccountResponse> getAccount(Long cuentaId) {

        try {
            CoreAccountResponse response = restClient
                    .get()
                    .uri("/internal/accounts/{cuentaId}", cuentaId)
                    .retrieve()
                    .body(CoreAccountResponse.class);

            return Optional.ofNullable(response);

        } catch (RestClientResponseException exception) {

            if (exception.getStatusCode().value() == 404) {
                return Optional.empty();
            }

            throw exception;
        }
    }

    public CoreWithdrawalResponse withdraw(
            Long cuentaId,
            BigDecimal monto) {

        try {
            return restClient
                    .post()
                    .uri(
                            "/internal/accounts/{cuentaId}/withdrawals",
                            cuentaId
                    )
                    .body(new CoreWithdrawalRequest(monto))
                    .retrieve()
                    .body(CoreWithdrawalResponse.class);

        } catch (RestClientResponseException exception) {

            throw new CoreApiException(
                    exception.getStatusCode(),
                    extractMessage(exception)
            );
        }
    }

    private Optional<CoreAccountResponse> getAccountFallback(
            Long cuentaId,
            Throwable throwable) {

        System.out.println(
                "Fallback Bank Core - cuenta " + cuentaId
                        + ": " + throwable.getClass().getSimpleName()
        );

        return Optional.empty();
    }

    private String extractMessage(
            RestClientResponseException exception) {

        String body = exception.getResponseBodyAsString();

        if (body.contains("Fondos insuficientes")) {
            return "Saldo insuficiente";
        }

        if (body.contains("No existe la cuenta")) {
            return "Cuenta no encontrada";
        }

        if (body.contains("monto")) {
            return "El monto del retiro debe ser mayor que cero";
        }

        return "Error al procesar la operación";
    }
}