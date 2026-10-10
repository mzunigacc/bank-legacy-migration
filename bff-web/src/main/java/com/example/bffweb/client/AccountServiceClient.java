package com.example.bffweb.client;

import com.example.bffweb.client.dto.CoreAccountResponse;
import com.example.bffweb.client.dto.CoreMovementResponse;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Optional;

@Component
public class AccountServiceClient {

    private static final String ACCOUNT_SERVICE_URL =
            "http://account-service";

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(ACCOUNT_SERVICE_URL)
                .build();
    }

    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "getAccountFallback"
    )
    @Bulkhead(
            name = "accountService",
            type = Bulkhead.Type.SEMAPHORE
    )
    public Optional<CoreAccountResponse> getAccount(Long cuentaId) {

        try {
            CoreAccountResponse response = restClient
                    .get()
                    .uri("/internal/accounts/{cuentaId}", cuentaId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            bearerToken()
                    )
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

    @Retry(name = "accountService")
    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "getMovementsFallback"
    )
    @Bulkhead(
            name = "accountService",
            type = Bulkhead.Type.SEMAPHORE
    )
    public List<CoreMovementResponse> getMovements(Long cuentaId) {

        CoreMovementResponse[] response = restClient
                .get()
                .uri(
                        "/internal/accounts/{cuentaId}/movements",
                        cuentaId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken()
                )
                .retrieve()
                .body(CoreMovementResponse[].class);

        return response == null
                ? List.of()
                : List.of(response);
    }

    private String bearerToken() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwt) {
            return "Bearer " + jwt.getToken().getTokenValue();
        }

        throw new IllegalStateException(
                "No existe un JWT autenticado para propagar"
        );
    }

    @SuppressWarnings("unused")
    private Optional<CoreAccountResponse> getAccountFallback(
            Long cuentaId,
            Throwable throwable) {

        System.err.printf(
                "[BFF-WEB] Account Service no disponible para cuenta %d: %s%n",
                cuentaId,
                throwable.getMessage()
        );

        return Optional.empty();
    }

    @SuppressWarnings("unused")
    private List<CoreMovementResponse> getMovementsFallback(
            Long cuentaId,
            Throwable throwable) {

        System.err.printf(
                "[BFF-WEB] movimientos no disponibles para cuenta %d: %s%n",
                cuentaId,
                throwable.getMessage()
        );

        return List.of();
    }
}
