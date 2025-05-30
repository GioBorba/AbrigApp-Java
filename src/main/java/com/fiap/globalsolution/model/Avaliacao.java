package com.fiap.globalsolution.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "avaliacoes")
public class Avaliacao {
    @Id
    private String id;

    @DBRef
    private Abrigo abrigo;

    @NotBlank
    private String usuarioUid; // Firebase UID

    @Min(1) @Max(5)
    private Integer nota;

    @Size(max = 500)
    private String comentario;

    private LocalDateTime dataCriacao = LocalDateTime.now();
}