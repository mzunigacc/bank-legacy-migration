package com.example.bankcore.controller;

import com.example.bankcore.entity.Account;
import com.example.bankcore.entity.AccountMovement;
import com.example.bankcore.service.AccountService;

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
}