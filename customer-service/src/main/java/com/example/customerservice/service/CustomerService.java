package com.example.customerservice.service;

import com.example.customerservice.dto.CustomerResponse;
import com.example.customerservice.dto.UpdateCustomerRequest;
import com.example.customerservice.entity.CustomerProfile;
import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerProfileRepository repository;

    public CustomerService(CustomerProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long cuentaId) {
        CustomerProfile customer = findCustomer(cuentaId);
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(
            Long cuentaId,
            UpdateCustomerRequest request
    ) {
        CustomerProfile customer = findCustomer(cuentaId);

        customer.setNombre(request.nombre().trim());
        customer.setEdad(request.edad());

        CustomerProfile updated = repository.save(customer);

        return toResponse(updated);
    }

    private CustomerProfile findCustomer(Long cuentaId) {
        return repository.findById(cuentaId)
                .orElseThrow(() -> new CustomerNotFoundException(cuentaId));
    }

    private CustomerResponse toResponse(CustomerProfile customer) {
        return new CustomerResponse(
                customer.getCuentaId(),
                customer.getNombre(),
                customer.getEdad()
        );
    }
}
