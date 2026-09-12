package com.example.banklegacymigration.bff.web.controller;

import com.example.banklegacymigration.bff.web.dto.WebAccountDetailResponse;
import com.example.banklegacymigration.bff.web.service.WebAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web/cuentas")
public class WebAccountController {

    private final WebAccountService webAccountService;

    public WebAccountController(WebAccountService webAccountService) {
        this.webAccountService = webAccountService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<WebAccountDetailResponse> getAccount(
            @PathVariable Long cuentaId) {

        return webAccountService.getAccount(cuentaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}