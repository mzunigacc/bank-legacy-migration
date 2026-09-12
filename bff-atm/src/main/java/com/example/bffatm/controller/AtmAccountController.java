package com.example.bffatm.controller;

import com.example.bffatm.dto.AtmBalanceResponse;
import com.example.bffatm.dto.WithdrawalRequest;
import com.example.bffatm.dto.WithdrawalResponse;
import com.example.bffatm.service.AtmAccountService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/atm/cuentas")
public class AtmAccountController {

    private final AtmAccountService atmAccountService;

    public AtmAccountController(
            AtmAccountService atmAccountService) {
        this.atmAccountService = atmAccountService;
    }

    @GetMapping("/{cuentaId}/saldo")
    public ResponseEntity<AtmBalanceResponse> getBalance(
            @PathVariable Long cuentaId) {

        return atmAccountService.getBalance(cuentaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{cuentaId}/retiros")
    public ResponseEntity<WithdrawalResponse> withdraw(
            @PathVariable Long cuentaId,
            @Valid @RequestBody WithdrawalRequest request) {

        WithdrawalResponse response =
                atmAccountService.withdraw(
                        cuentaId,
                        request.getMonto()
                );

        return ResponseEntity.ok(response);
    }
}