package com.fiap.globalsolution.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequestDTO(
        @NotBlank
        String abrigoId,

        @Min(1) @Max(5)
        Integer nota,

        @Size(max = 500)
        String comentario
) {}
