package com.fiap.globalsolution.dto;

import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        String id,
        String abrigoId,
        String usuarioUid,
        int nota,
        String comentario,
        LocalDateTime dataCriacao
) {}
