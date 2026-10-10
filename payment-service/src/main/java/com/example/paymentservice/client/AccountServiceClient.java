package com.example.paymentservice.client;

import com.example.paymentservice.dto.DebitRequest;
import com.example.paymentservice.dto.DebitResult;
import com.example.paymentservice.exception.AccountNotFoundException;
import com.example.paymentservice.exception.InsufficientFundsException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class AccountServiceClient {

    private static final String ACCOUNT_SERVICE_URL = "http://account-service";

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl(ACCOUNT_SERVICE_URL)
                .build();
    }

    public DebitResult debit(Long cuentaId, BigDecimal monto, String bearerToken) {
        return restClient.post()
                .uri("/internal/accounts/{cuentaId}/debit", cuentaId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(new DebitRequest(monto))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new AccountNotFoundException(cuentaId);
                        }
                )
                .onStatus(
                        status -> status.value() == 409,
                        (request, response) -> {
                            throw new InsufficientFundsException();
                        }
                )
                .body(DebitResult.class);
    }
}
