package com.fiap.globalsolution.controller.web;

import com.fiap.globalsolution.dto.AvaliacaoRequestDTO;
import com.fiap.globalsolution.dto.AvaliacaoResponseDTO;
import com.fiap.globalsolution.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    // Listar avaliações de um abrigo (público)
    @GetMapping("/abrigo/{abrigoId}")
    public String listarPorAbrigo(@PathVariable String abrigoId, Model model) {
        List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarPorAbrigo(abrigoId);
        model.addAttribute("avaliacoes", avaliacoes);
        model.addAttribute("abrigoId", abrigoId);
        return "avaliacoes/lista";
    }

    // Formulário para nova avaliação (usuário autenticado)
    @GetMapping("/novo/{abrigoId}")
    public String novaAvaliacaoForm(@PathVariable String abrigoId, Model model) {
        model.addAttribute("abrigoId", abrigoId);
        model.addAttribute("avaliacaoRequestDTO", new AvaliacaoRequestDTO(abrigoId, null, null));
        return "avaliacoes/form";
    }

    @GetMapping("/minhas-avaliacoes")
    public String listarMinhasAvaliacoes(Model model, Authentication auth) {
        List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarPorUsuario(auth);
        model.addAttribute("avaliacoes", avaliacoes);
        return "avaliacoes/minhas-avaliacoes";
    }


    // Submeter nova avaliação
    @PostMapping
    public String criar(@Valid @ModelAttribute("avaliacaoRequestDTO") AvaliacaoRequestDTO dto,
                        BindingResult result,
                        Authentication auth) {
        if (result.hasErrors()) {
            return "avaliacoes/form";
        }
        avaliacaoService.criarAvaliacao(dto, auth);
        return "redirect:/abrigos/" + dto.abrigoId();
    }

    // Formulário de edição (apenas autor)
    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable String id,
                             @RequestParam("abrigoId") String abrigoId,
                             Model model,
                             Authentication auth) {
        List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarPorAbrigo(abrigoId);
        AvaliacaoResponseDTO avaliacao = avaliacoes.stream()
                .filter(av -> av.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO(avaliacao.abrigoId(), avaliacao.nota(), avaliacao.comentario());
        model.addAttribute("avaliacaoRequestDTO", dto);
        model.addAttribute("avaliacaoId", id);
        model.addAttribute("abrigoId", abrigoId);
        return "avaliacoes/form";
    }

    // Atualizar avaliação
    @PostMapping("/{id}")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("avaliacaoRequestDTO") AvaliacaoRequestDTO dto,
                            BindingResult result,
                            Authentication auth) {
        if (result.hasErrors()) {
            return "avaliacoes/form";
        }

        avaliacaoService.editarAvaliacao(id, dto, auth);
        return "redirect:/abrigos/" + dto.abrigoId();
    }

    // Excluir avaliação
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id,
                          @RequestParam("abrigoId") String abrigoId,
                          Authentication auth) {
        avaliacaoService.deletarAvaliacao(id, auth);
        return "redirect:/abrigos/" + abrigoId;
    }
}
