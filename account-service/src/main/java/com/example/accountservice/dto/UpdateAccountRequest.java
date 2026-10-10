package com.example.accountservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        String tipo
) {
}
