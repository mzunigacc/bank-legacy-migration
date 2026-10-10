package com.example.bffweb.client;

import com.example.bffweb.client.dto.CustomerServiceResponse;
import com.example.bffweb.dto.UpdateWebCustomerRequest;

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

import java.util.Optional;

@Component
public class CustomerServiceClient {

    private static final String CUSTOMER_SERVICE_URL =
            "http://customer-service";

    private final RestClient restClient;

    public CustomerServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(CUSTOMER_SERVICE_URL)
                .build();
    }

    @Retry(name = "customerService")
    @CircuitBreaker(
            name = "customerService",
            fallbackMethod = "getCustomerFallback"
    )
    @Bulkhead(
            name = "customerService",
            type = Bulkhead.Type.SEMAPHORE
    )
    public Optional<CustomerServiceResponse> getCustomer(Long cuentaId) {

        try {
            CustomerServiceResponse response = restClient
                    .get()
                    .uri("/internal/customers/{cuentaId}", cuentaId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            bearerToken()
                    )
                    .retrieve()
                    .body(CustomerServiceResponse.class);

            return Optional.ofNullable(response);

        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().isSameCodeAs(
                    HttpStatusCode.valueOf(404))) {
                return Optional.empty();
            }

            throw exception;
        }
    }

    /*
     * PUT modifica estado:
     * CircuitBreaker + Bulkhead, pero NO Retry automático.
     */
    @CircuitBreaker(name = "customerService")
    @Bulkhead(
            name = "customerService",
            type = Bulkhead.Type.SEMAPHORE
    )
    public CustomerServiceResponse updateCustomer(
            Long cuentaId,
            UpdateWebCustomerRequest request) {

        return restClient
                .put()
                .uri("/internal/customers/{cuentaId}", cuentaId)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        bearerToken()
                )
                .body(request)
                .retrieve()
                .body(CustomerServiceResponse.class);
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
    private Optional<CustomerServiceResponse> getCustomerFallback(
            Long cuentaId,
            Throwable throwable) {

        System.err.printf(
                "[BFF-WEB] Customer Service no disponible para cuenta %d: %s%n",
                cuentaId,
                throwable.getMessage()
        );

        return Optional.empty();
    }
}
