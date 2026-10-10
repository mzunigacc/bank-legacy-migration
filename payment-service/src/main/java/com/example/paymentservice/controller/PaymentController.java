package com.example.paymentservice.controller;

import com.example.paymentservice.dto.DepositRequest;
import com.example.paymentservice.dto.PaymentResult;
import com.example.paymentservice.dto.TransferPaymentRequest;
import com.example.paymentservice.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/deposits")
    public ResponseEntity<PaymentResult> deposit(
            @Valid @RequestBody DepositRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return ResponseEntity.ok(
                paymentService.deposit(
                        request.cuentaDestinoId(),
                        request.monto(),
                        authorization
                )
        );
    }

    @PostMapping("/transfers")
    public ResponseEntity<PaymentResult> transfer(
            @Valid @RequestBody TransferPaymentRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authorization) {

        return ResponseEntity.ok(
                paymentService.transfer(
                        request.cuentaOrigenId(),
                        request.cuentaDestinoId(),
                        request.monto(),
                        authorization
                )
        );
    }
}
