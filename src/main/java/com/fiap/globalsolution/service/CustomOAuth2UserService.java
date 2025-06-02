package com.fiap.globalsolution.service;

import com.fiap.globalsolution.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioService usuarioService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> atributos = oAuth2User.getAttributes();

        String nome = (String) atributos.get("name");
        String email = (String) atributos.get("email");

        // Usa o service para encapsular a lógica de criação ou busca
        Usuario usuario = usuarioService.criarOuBuscarPorEmail(email, nome);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())),
                atributos,
                "email"
        );
    }
}
