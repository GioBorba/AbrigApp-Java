package com.fiap.globalsolution.dto;

public record AbrigoResponseDTO(
        String id,
        String nome,
        String endereco,
        String cidade,
        String estado,
        Double latitude,
        Double longitude,
        Boolean ativo
) {}
