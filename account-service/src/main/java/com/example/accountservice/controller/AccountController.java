package com.example.accountservice.controller;

import com.example.accountservice.dto.CreateAccountRequest;
import com.example.accountservice.dto.DebitRequest;
import com.example.accountservice.dto.DebitResult;
import com.example.accountservice.dto.UpdateAccountRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.AccountMovement;
import com.example.accountservice.service.AccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<Account> getAccount(
            @PathVariable Long cuentaId) {

        return accountService.getAccount(cuentaId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
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

    @PutMapping("/{cuentaId}")
    public ResponseEntity<Account> updateAccount(
            @PathVariable Long cuentaId,
            @Valid @RequestBody UpdateAccountRequest request) {

        return ResponseEntity.ok(
                accountService.updateAccount(
                        cuentaId,
                        request
                )
        );
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long cuentaId) {

        accountService.closeAccount(cuentaId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cuentaId}/debit")
    public ResponseEntity<DebitResult> debit(
            @PathVariable Long cuentaId,
            @Valid @RequestBody DebitRequest request) {

        return ResponseEntity.ok(
                accountService.debit(
                        cuentaId,
                        request.getMonto()
                )
        );
    }
}
