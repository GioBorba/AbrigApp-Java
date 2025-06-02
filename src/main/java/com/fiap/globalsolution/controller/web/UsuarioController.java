package com.fiap.globalsolution.controller.web;

import com.fiap.globalsolution.dto.UsuarioDTO;
import com.fiap.globalsolution.model.Role;
import com.fiap.globalsolution.model.Usuario;
import com.fiap.globalsolution.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/me")
    public String dadosUsuarioAutenticado(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        usuario.ifPresent(u -> model.addAttribute("usuario", u));
        return "usuarios/detalhes"; // página que mostra nome, email, role do usuário logado
    }

    @PostMapping("/{id}/role")
    public String atualizarRole(@PathVariable String id, @RequestParam Role role) {
        usuarioService.atualizarRole(id, role);
        return "redirect:/usuarios";
    }
}
