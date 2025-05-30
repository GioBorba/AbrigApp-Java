package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AbrigoRequestDTO(
        @NotBlank
        String nome,

        @NotBlank
        String endereco,

        @NotBlank
        String cidade,

        @NotBlank
        @Size(min = 2, max = 2)
        String estado,

        @DecimalMin("-90.0") @DecimalMax("90.0")
        Double latitude,

        @DecimalMin("-180.0") @DecimalMax("180.0")
        Double longitude
) {}
