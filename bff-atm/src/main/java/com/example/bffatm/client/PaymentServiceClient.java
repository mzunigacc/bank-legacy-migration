package com.example.bffatm.client;

import com.example.bffatm.client.dto.CoreWithdrawalRequest;
import com.example.bffatm.client.dto.CoreWithdrawalResponse;
import com.example.bffatm.exception.CoreApiException;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

@Component
public class PaymentServiceClient {

    private static final String PAYMENT_SERVICE_URL =
            "http://payment-service";

    private final RestClient restClient;

    public PaymentServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(PAYMENT_SERVICE_URL)
                .build();
    }

    /*
     * No se aplica Retry a retiros porque la operación modifica estado.
     * Un reintento automático podría provocar un retiro duplicado.
     */
    @CircuitBreaker(name = "paymentService")
    @Bulkhead(
            name = "paymentService",
            type = Bulkhead.Type.SEMAPHORE
    )
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
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            bearerToken()
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
                "No existe un token OAuth2 autenticado para propagar a Payment Service"
        );
    }

    private String extractMessage(
            RestClientResponseException exception) {

        String body = exception.getResponseBodyAsString();

        if (body.contains("Fondos insuficientes")) {
            return "Saldo insuficiente";
        }

        if (body.contains("Cuenta no encontrada")
                || body.contains("ACCOUNT_NOT_FOUND")) {
            return "Cuenta no encontrada";
        }

        if (body.contains("monto")
                || body.contains("VALIDATION_ERROR")) {
            return "El monto del retiro debe ser mayor que cero";
        }

        if (body.contains("ACCOUNT_SERVICE_UNAVAILABLE")) {
            return "Servicio de cuentas no disponible";
        }

        return "Error al procesar la operación";
    }
}
