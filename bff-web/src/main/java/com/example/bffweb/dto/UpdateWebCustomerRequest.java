package com.example.bffweb.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateWebCustomerRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 0, message = "La edad no puede ser negativa")
        @Max(value = 120, message = "La edad no puede ser mayor a 120")
        Integer edad
) {
}
