package com.example.customerservice.controller;

import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.UpdateCustomerRequest;
import com.example.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable Long cuentaId
    ) {
        return ResponseEntity.ok(
                customerService.getCustomer(cuentaId)
        );
    }

    @PutMapping("/{cuentaId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long cuentaId,
            @Valid @RequestBody UpdateCustomerRequest request
    ) {
        return ResponseEntity.ok(
                customerService.updateCustomer(cuentaId, request)
        );
    }
}
