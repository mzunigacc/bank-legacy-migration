package com.example.bankcore.controller;

import com.example.bankcore.dto.WithdrawalRequest;
import com.example.bankcore.dto.WithdrawalResult;
import com.example.bankcore.service.WithdrawalService;

import jakarta.validation.Valid;

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
            @Valid @RequestBody WithdrawalRequest request) {

        return ResponseEntity.ok(
                withdrawalService.withdraw(cuentaId, request.getMonto())
        );
    }
}