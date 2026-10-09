package com.example.accountservice.controller;

import com.example.accountservice.dto.DebitRequest;
import com.example.accountservice.dto.DebitResult;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.AccountMovement;
import com.example.accountservice.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<Account> getAccount(@PathVariable Long cuentaId) {
        return accountService.getAccount(cuentaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{cuentaId}/movements")
    public ResponseEntity<List<AccountMovement>> getMovements(
            @PathVariable Long cuentaId) {

        if (accountService.getAccount(cuentaId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                accountService.getMovements(cuentaId)
        );
    }

    @PostMapping("/{cuentaId}/debit")
    public ResponseEntity<DebitResult> debit(
            @PathVariable Long cuentaId,
            @Valid @RequestBody DebitRequest request) {

        return ResponseEntity.ok(
                accountService.debit(cuentaId, request.getMonto())
        );
    }
}
