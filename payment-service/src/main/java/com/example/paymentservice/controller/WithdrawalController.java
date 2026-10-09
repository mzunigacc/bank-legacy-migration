package com.example.paymentservice.controller;

import com.example.paymentservice.dto.WithdrawalRequest;
import com.example.paymentservice.dto.WithdrawalResult;
import com.example.paymentservice.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/{cuentaId}/withdrawals")
    public ResponseEntity<WithdrawalResult> withdraw(
            @PathVariable Long cuentaId,
            @Valid @RequestBody WithdrawalRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ResponseEntity.ok(
                withdrawalService.withdraw(
                        cuentaId,
                        request.getMonto(),
                        authorization
                )
        );
    }
}
