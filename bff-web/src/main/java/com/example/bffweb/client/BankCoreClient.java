package com.example.bffweb.client;

import com.example.bffweb.client.dto.CoreAccountResponse;
import com.example.bffweb.client.dto.CoreMovementResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
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

            if (exception.getStatusCode().isSameCodeAs(
                    HttpStatusCode.valueOf(404))) {
                return Optional.empty();
            }

            throw exception;
        }
    }

    @CircuitBreaker(
            name = "bankCore",
            fallbackMethod = "getMovementsFallback"
    )
    public List<CoreMovementResponse> getMovements(Long cuentaId) {

        List<CoreMovementResponse> response = restClient
                .get()
                .uri("/internal/accounts/{cuentaId}/movements", cuentaId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return response != null
                ? response
                : List.of();
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

    private List<CoreMovementResponse> getMovementsFallback(
            Long cuentaId,
            Throwable throwable) {

        System.out.println(
                "Fallback Bank Core - movimientos cuenta " + cuentaId
                        + ": " + throwable.getClass().getSimpleName()
        );

        return List.of();
    }
}