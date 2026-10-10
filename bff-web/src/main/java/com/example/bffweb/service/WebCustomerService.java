package com.example.bffweb.service;

import com.example.bffweb.client.CustomerServiceClient;
import com.example.bffweb.client.dto.CustomerServiceResponse;
import com.example.bffweb.dto.UpdateWebCustomerRequest;
import com.example.bffweb.dto.WebCustomerResponse;

import org.springframework.stereotype.Service;

@Service
public class WebCustomerService {

    private final CustomerServiceClient customerServiceClient;

    public WebCustomerService(
            CustomerServiceClient customerServiceClient) {

        this.customerServiceClient = customerServiceClient;
    }

    public WebCustomerResponse getCustomer(Long cuentaId) {

        CustomerServiceResponse customer =
                customerServiceClient
                        .getCustomer(cuentaId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cliente no encontrado"
                                )
                        );

        return toResponse(customer);
    }

    public WebCustomerResponse updateCustomer(
            Long cuentaId,
            UpdateWebCustomerRequest request) {

        return toResponse(
                customerServiceClient.updateCustomer(
                        cuentaId,
                        request
                )
        );
    }

    private WebCustomerResponse toResponse(
            CustomerServiceResponse customer) {

        return new WebCustomerResponse(
                customer.cuentaId(),
                customer.nombre(),
                customer.edad()
        );
    }
}
