package com.fiap.globalsolution.controller.api;

import com.fiap.globalsolution.dto.AvaliacaoRequestDTO;
import com.fiap.globalsolution.dto.AvaliacaoResponseDTO;
import com.fiap.globalsolution.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin
public class AvaliacoesRestController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    // Listar avaliações de um abrigo específico - público
    @GetMapping("/abrigo/{abrigoId}")
    public List<AvaliacaoResponseDTO> listarPorAbrigo(@PathVariable String abrigoId) {
        return avaliacaoService.listarPorAbrigo(abrigoId);
    }

    // Criar nova avaliação - precisa estar autenticado (via Firebase Token)
    @PostMapping
    public AvaliacaoResponseDTO criarAvaliacao(@Valid @RequestBody AvaliacaoRequestDTO dto, Authentication auth) {
        return avaliacaoService.criarAvaliacao(dto, auth);
    }

    // Listar avaliações do usuário logado
    @GetMapping("/minhas")
    public List<AvaliacaoResponseDTO> listarMinhasAvaliacoes(Authentication auth) {
        return avaliacaoService.listarPorUsuario(auth);
    }

    // Editar avaliação - só dono pode editar
    @PutMapping("/{id}")
    public AvaliacaoResponseDTO editarAvaliacao(@PathVariable String id,
                                                @Valid @RequestBody AvaliacaoRequestDTO dto,
                                                Authentication auth) {
        return avaliacaoService.editarAvaliacao(id, dto, auth);
    }

    // Excluir avaliação - só dono pode excluir
    @DeleteMapping("/{id}")
    public void deletarAvaliacao(@PathVariable String id, Authentication auth) {
        avaliacaoService.deletarAvaliacao(id, auth);
    }
}
