package com.example.bffmobile.controller;

import com.example.bffmobile.dto.MobileAccountDetailResponse;
import com.example.bffmobile.service.MobileAccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile/cuentas")
public class MobileAccountController {

    private final MobileAccountService mobileAccountService;

    public MobileAccountController(
            MobileAccountService mobileAccountService) {
        this.mobileAccountService = mobileAccountService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<MobileAccountDetailResponse> getAccount(
            @PathVariable Long cuentaId) {

        return mobileAccountService.getAccount(cuentaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}