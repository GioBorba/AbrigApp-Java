package com.fiap.globalsolution.dto;

import com.fiap.globalsolution.model.Role;

public record UsuarioDTO(
        String id,
        String nome,
        String email,
        Role role
) {}
