package com.example.bffatm.client;

import com.example.bffatm.client.dto.CoreAccountResponse;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
public class AccountServiceClient {

    private static final String ACCOUNT_SERVICE_URL =
            "http://account-service";

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder builder) {
        this.restClient = builder
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

            if (exception.getStatusCode().value() == 404) {
                return Optional.empty();
            }

            throw exception;
        }
    }

    private String bearerToken() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return "Bearer "
                    + jwtAuthentication
                            .getToken()
                            .getTokenValue();
        }

        throw new IllegalStateException(
                "No existe un token OAuth2 autenticado para propagar a Account Service"
        );
    }

    private Optional<CoreAccountResponse> getAccountFallback(
            Long cuentaId,
            Throwable throwable) {

        System.out.println(
                "Fallback Account Service - cuenta "
                        + cuentaId
                        + ": "
                        + throwable.getClass().getSimpleName()
        );

        return Optional.empty();
    }
}
