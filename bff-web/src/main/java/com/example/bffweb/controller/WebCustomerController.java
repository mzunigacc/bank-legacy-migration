package com.example.bffweb.controller;

import com.example.bffweb.dto.UpdateWebCustomerRequest;
import com.example.bffweb.dto.WebCustomerResponse;
import com.example.bffweb.service.WebCustomerService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/web/clientes")
public class WebCustomerController {

    private final WebCustomerService webCustomerService;

    public WebCustomerController(
            WebCustomerService webCustomerService) {

        this.webCustomerService = webCustomerService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<WebCustomerResponse> getCustomer(
            @PathVariable Long cuentaId) {

        return ResponseEntity.ok(
                webCustomerService.getCustomer(cuentaId)
        );
    }

    @PutMapping("/{cuentaId}")
    public ResponseEntity<WebCustomerResponse> updateCustomer(
            @PathVariable Long cuentaId,
            @Valid @RequestBody UpdateWebCustomerRequest request) {

        return ResponseEntity.ok(
                webCustomerService.updateCustomer(
                        cuentaId,
                        request
                )
        );
    }
}
