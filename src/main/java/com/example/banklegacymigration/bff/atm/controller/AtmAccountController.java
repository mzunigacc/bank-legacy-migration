package com.example.banklegacymigration.bff.atm.controller;

import com.example.banklegacymigration.bff.atm.dto.AtmBalanceResponse;
import com.example.banklegacymigration.bff.atm.dto.WithdrawalRequest;
import com.example.banklegacymigration.bff.atm.dto.WithdrawalResponse;
import com.example.banklegacymigration.bff.atm.service.AtmAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody WithdrawalRequest request) {

        WithdrawalResponse response =
                atmAccountService.withdraw(
                        cuentaId,
                        request.getMonto()
                );

        return ResponseEntity.ok(response);
    }
}