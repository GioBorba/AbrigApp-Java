package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.AvaliacaoRequestDTO;
import com.fiap.globalsolution.dto.AvaliacaoResponseDTO;
import com.fiap.globalsolution.model.Abrigo;
import com.fiap.globalsolution.model.Avaliacao;
import com.fiap.globalsolution.rabbitmq.AvaliacaoProducer;
import com.fiap.globalsolution.repository.AbrigoRepository;
import com.fiap.globalsolution.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AbrigoRepository abrigoRepository;

    @Mock
    private AvaliacaoProducer avaliacaoProducer;

    @Mock
    private Authentication auth;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void criarAvaliacao_deveCriarEAvaliarAbrigo() {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO("123", 5, "Muito bom!");
        Abrigo abrigo = new Abrigo();
        abrigo.setId("123");

        when(abrigoRepository.findById("123")).thenReturn(Optional.of(abrigo));
        when(auth.getName()).thenReturn("usuario123");
        when(avaliacaoRepository.save(any())).thenAnswer(inv -> {
            Avaliacao av = inv.getArgument(0);
            av.setId("av001");
            return av;
        });

        AvaliacaoResponseDTO response = avaliacaoService.criarAvaliacao(dto, auth);

        assertThat(response.comentario()).isEqualTo("Muito bom!");
        assertThat(response.nota()).isEqualTo(5);
        assertThat(response.usuarioUid()).isEqualTo("usuario123");
        verify(avaliacaoProducer).enviarMensagem(contains("usuario123"));
    }

    @Test
    void criarAvaliacao_quandoAbrigoNaoExiste_deveLancarExcecao() {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO("999", 4, "Comentário");

        when(abrigoRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> avaliacaoService.criarAvaliacao(dto, auth))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Abrigo não encontrado");
    }

    @Test
    void listarPorAbrigo_deveRetornarListaDeDTOs() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .nota(4)
                .comentario("Comentário")
                .usuarioUid("user")
                .dataCriacao(LocalDateTime.now())
                .abrigo(new Abrigo())
                .build();

        when(avaliacaoRepository.findByAbrigoId("abc")).thenReturn(List.of(avaliacao));

        List<AvaliacaoResponseDTO> resultado = avaliacaoService.listarPorAbrigo("abc");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).comentario()).isEqualTo("Comentário");
    }

    @Test
    void listarPorUsuario_deveRetornarSomenteDoUsuarioLogado() {
        Avaliacao av = Avaliacao.builder()
                .id("a1")
                .usuarioUid("usuario123")
                .nota(5)
                .comentario("Muito bom")
                .abrigo(new Abrigo())
                .dataCriacao(LocalDateTime.now())
                .build();

        when(auth.getName()).thenReturn("usuario123");
        when(avaliacaoRepository.findByUsuarioUid("usuario123")).thenReturn(List.of(av));

        List<AvaliacaoResponseDTO> lista = avaliacaoService.listarPorUsuario(auth);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).usuarioUid()).isEqualTo("usuario123");
    }

    @Test
    void editarAvaliacao_quandoUsuarioEhDono_deveAtualizar() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .usuarioUid("usuario123")
                .nota(3)
                .comentario("Ok")
                .abrigo(new Abrigo())
                .dataCriacao(LocalDateTime.now())
                .build();

        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO("abrigo1", 5, "Novo comentário");

        when(auth.getName()).thenReturn("usuario123");
        when(avaliacaoRepository.findById("a1")).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AvaliacaoResponseDTO result = avaliacaoService.editarAvaliacao("a1", dto, auth);

        assertThat(result.nota()).isEqualTo(5);
        assertThat(result.comentario()).isEqualTo("Novo comentário");
    }

    @Test
    void editarAvaliacao_quandoUsuarioNaoEhDono_deveLancarExcecao() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .usuarioUid("outroUser")
                .nota(3)
                .comentario("Ok")
                .build();

        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO("abrigo1", 5, "Novo comentário");

        when(auth.getName()).thenReturn("usuario123");
        when(avaliacaoRepository.findById("a1")).thenReturn(Optional.of(avaliacao));

        assertThatThrownBy(() -> avaliacaoService.editarAvaliacao("a1", dto, auth))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Sem permissão para editar esta avaliação");
    }

    @Test
    void deletarAvaliacao_quandoDono_deveExcluir() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .usuarioUid("usuario123")
                .build();

        when(auth.getName()).thenReturn("usuario123");
        when(auth.getAuthorities()).thenReturn(List.of());
        when(avaliacaoRepository.findById("a1")).thenReturn(Optional.of(avaliacao));

        avaliacaoService.deletarAvaliacao("a1", auth);

        verify(avaliacaoRepository).deleteById("a1");
    }

    @Test
    void deletarAvaliacao_quandoAdmin_deveExcluir() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .usuarioUid("outraPessoa")
                .build();

        when(auth.getName()).thenReturn("admin");

        when(auth.getAuthorities()).thenReturn(
                (Collection) List.of(new SimpleGrantedAuthority("ADMIN"))
        );


        when(avaliacaoRepository.findById("a1")).thenReturn(Optional.of(avaliacao));

        avaliacaoService.deletarAvaliacao("a1", auth);

        verify(avaliacaoRepository).deleteById("a1");
    }

    @Test
    void deletarAvaliacao_quandoNaoAutorizado_deveLancarExcecao() {
        Avaliacao avaliacao = Avaliacao.builder()
                .id("a1")
                .usuarioUid("outraPessoa")
                .build();

        when(auth.getName()).thenReturn("usuario123");
        when(auth.getAuthorities()).thenReturn(List.of());
        when(avaliacaoRepository.findById("a1")).thenReturn(Optional.of(avaliacao));

        assertThatThrownBy(() -> avaliacaoService.deletarAvaliacao("a1", auth))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Sem permissão para excluir esta avaliação");
    }
}
