package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.UsuarioDTO;
import com.fiap.globalsolution.model.Role;
import com.fiap.globalsolution.model.Usuario;
import com.fiap.globalsolution.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public Usuario criarOuBuscarPorEmail(String email, String nome) {
        return usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novoUsuario = Usuario.builder()
                            .email(email)
                            .nome(nome)
                            .role(Role.USER)
                            .build();
                    return usuarioRepository.save(novoUsuario);
                });
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.getRole()))
                .toList();
    }

    public Optional<UsuarioDTO> atualizarRole(String idUsuario, Role novaRole) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> {
                    usuario.setRole(novaRole);
                    return usuarioRepository.save(usuario);
                })
                .map(usuarioAtualizado -> new UsuarioDTO(
                        usuarioAtualizado.getId(),
                        usuarioAtualizado.getNome(),
                        usuarioAtualizado.getEmail(),
                        usuarioAtualizado.getRole()
                ));
    }
}
