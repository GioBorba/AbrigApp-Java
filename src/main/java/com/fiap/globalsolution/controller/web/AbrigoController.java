package com.fiap.globalsolution.controller.web;

import com.fiap.globalsolution.dto.AbrigoRequestDTO;
import com.fiap.globalsolution.dto.AbrigoResponseDTO;
import com.fiap.globalsolution.dto.AvaliacaoResponseDTO;
import com.fiap.globalsolution.model.Role;
import com.fiap.globalsolution.service.AbrigoService;
import com.fiap.globalsolution.service.AvaliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/abrigos")
@RequiredArgsConstructor
public class AbrigoController {

    private final AbrigoService abrigoService;
    private final AvaliacaoService avaliacaoService;

    @GetMapping("/lista")
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) String cidade,
                         Model model) {
        List<AbrigoResponseDTO> abrigos = abrigoService.filtrar(estado, cidade);
        model.addAttribute("abrigos", abrigos);
        return "abrigos/lista";
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable String id, Model model) {
        AbrigoResponseDTO abrigo = abrigoService.buscarPorId(id);
        List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarPorAbrigo(id);

        model.addAttribute("abrigo", abrigo);
        model.addAttribute("avaliacoes", avaliacoes);
        return "abrigos/detalhes";
    }

    @GetMapping("/novo")
    @PreAuthorize("hasRole('ADMIN')")
    public String novoForm(Model model) {
        model.addAttribute("abrigoRequestDTO", new AbrigoRequestDTO(null, null, null, null, null, null));
        model.addAttribute("formAction", "/abrigos/salvar");
        return "abrigos/form";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasRole('ADMIN')")
    public String criar(@Valid @ModelAttribute("abrigoRequestDTO") AbrigoRequestDTO dto,
                        BindingResult result) {
        if (result.hasErrors()) {
            return "abrigos/form";
        }
        abrigoService.criar(dto);
        return "redirect:/abrigos/lista";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN')")
    public String editarForm(@PathVariable String id, Model model) {
        AbrigoResponseDTO abrigo = abrigoService.buscarPorId(id);
        AbrigoRequestDTO dto = new AbrigoRequestDTO(
                abrigo.nome(),
                abrigo.endereco(),
                abrigo.cidade(),
                abrigo.estado(),
                abrigo.latitude(),
                abrigo.longitude()
        );
        model.addAttribute("abrigoRequestDTO", dto);
        model.addAttribute("formAction", "/abrigos/" + id);
        return "abrigos/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String atualizar(@PathVariable String id,
                            @Valid @ModelAttribute("abrigoRequestDTO") AbrigoRequestDTO dto,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "abrigos/form";
        }
        abrigoService.atualizar(id, dto, Role.ADMIN);
        return "redirect:/abrigos/" + id;
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable String id) {
        abrigoService.deletar(id, Role.ADMIN);
        return "redirect:/abrigos";
    }
}
