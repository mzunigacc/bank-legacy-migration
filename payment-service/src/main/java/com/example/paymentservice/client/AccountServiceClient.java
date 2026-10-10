package com.example.paymentservice.client;

import com.example.paymentservice.dto.DebitRequest;
import com.example.paymentservice.dto.CreditRequest;
import com.example.paymentservice.dto.CreditResult;
import com.example.paymentservice.dto.AccountTransferRequest;
import com.example.paymentservice.dto.AccountTransferResult;
import com.example.paymentservice.dto.DebitResult;
import com.example.paymentservice.exception.AccountNotFoundException;
import com.example.paymentservice.exception.AccountServiceUnavailableException;
import com.example.paymentservice.exception.InsufficientFundsException;
import com.example.paymentservice.exception.InvalidTransferException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "debitFallback"
    )
    @Bulkhead(name = "accountService")
    public DebitResult debit(
            Long cuentaId,
            BigDecimal monto,
            String bearerToken
    ) {
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

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "creditFallback"
    )
    @Bulkhead(name = "accountService")
    public CreditResult credit(
            Long cuentaId,
            BigDecimal monto,
            String bearerToken
    ) {

        return restClient.post()
                .uri("/internal/accounts/{cuentaId}/credit", cuentaId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(new CreditRequest(monto))
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new AccountNotFoundException(cuentaId);
                        }
                )
                .body(CreditResult.class);
    }

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod = "transferFallback"
    )
    @Bulkhead(name = "accountService")
    public AccountTransferResult transfer(
            Long cuentaOrigenId,
            Long cuentaDestinoId,
            BigDecimal monto,
            String bearerToken
    ) {

        return restClient.post()
                .uri("/internal/accounts/{cuentaId}/transfer", cuentaOrigenId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .body(new AccountTransferRequest(
                        cuentaDestinoId,
                        monto
                ))
                .retrieve()
                .onStatus(
                        status -> status.value() == 400,
                        (request, response) -> {
                            throw new InvalidTransferException();
                        }
                )
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new AccountNotFoundException(cuentaOrigenId);
                        }
                )
                .onStatus(
                        status -> status.value() == 409,
                        (request, response) -> {
                            throw new InsufficientFundsException();
                        }
                )
                .body(AccountTransferResult.class);
    }

    private CreditResult creditFallback(
            Long cuentaId,
            BigDecimal monto,
            String bearerToken,
            Throwable throwable
    ) {

        if (throwable instanceof AccountNotFoundException accountNotFound) {
            throw accountNotFound;
        }

        throw new AccountServiceUnavailableException(throwable);
    }

    private AccountTransferResult transferFallback(
            Long cuentaOrigenId,
            Long cuentaDestinoId,
            BigDecimal monto,
            String bearerToken,
            Throwable throwable
    ) {

        if (throwable instanceof AccountNotFoundException accountNotFound) {
            throw accountNotFound;
        }

        if (throwable instanceof InsufficientFundsException insufficientFunds) {
            throw insufficientFunds;
        }

        if (throwable instanceof InvalidTransferException invalidTransfer) {
            throw invalidTransfer;
        }

        throw new AccountServiceUnavailableException(throwable);
    }

    private DebitResult debitFallback(
            Long cuentaId,
            BigDecimal monto,
            String bearerToken,
            Throwable throwable
    ) {
        if (throwable instanceof AccountNotFoundException accountNotFound) {
            throw accountNotFound;
        }

        if (throwable instanceof InsufficientFundsException insufficientFunds) {
            throw insufficientFunds;
        }

        throw new AccountServiceUnavailableException(throwable);
    }
}
