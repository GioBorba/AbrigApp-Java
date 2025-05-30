package com.fiap.globalsolution.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    @NotBlank
    @Email
    private String email;
    private String nome;
    private String providerId;

    private Set<Role> roles = new HashSet<>(Set.of(Role.USER)); // Valor padrão
}