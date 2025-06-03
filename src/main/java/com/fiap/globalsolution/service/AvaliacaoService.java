package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.AvaliacaoRequestDTO;
import com.fiap.globalsolution.dto.AvaliacaoResponseDTO;
import com.fiap.globalsolution.model.Abrigo;
import com.fiap.globalsolution.model.Avaliacao;
import com.fiap.globalsolution.rabbitmq.AvaliacaoProducer;
import com.fiap.globalsolution.repository.AbrigoRepository;
import com.fiap.globalsolution.repository.AvaliacaoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AbrigoRepository abrigoRepository;

    @Autowired
    private AvaliacaoProducer avaliacaoProducer;

    public AvaliacaoResponseDTO criarAvaliacao(@Valid AvaliacaoRequestDTO dto, Authentication auth) {
        String usuarioId = auth.getName();

        Optional<Abrigo> optionalAbrigo = abrigoRepository.findById(dto.abrigoId());
        if (optionalAbrigo.isEmpty()) {
            throw new IllegalArgumentException("Abrigo não encontrado");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .abrigo(optionalAbrigo.get())
                .usuarioUid(usuarioId)
                .nota(dto.nota())
                .comentario(dto.comentario())
                .dataCriacao(LocalDateTime.now())
                .build();

        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        avaliacaoProducer.enviarMensagem("Avaliação feita pelo usuário: " + usuarioId);


        return new AvaliacaoResponseDTO(
                salva.getId(),
                salva.getAbrigo().getId(),
                salva.getUsuarioUid(),
                salva.getNota(),
                salva.getComentario(),
                salva.getDataCriacao()
        );
    }

    public List<AvaliacaoResponseDTO> listarPorAbrigo(String abrigoId) {
        List<Avaliacao> lista = avaliacaoRepository.findByAbrigoId(abrigoId);

        return lista.stream()
                .map(av -> new AvaliacaoResponseDTO(
                        av.getId(),
                        av.getAbrigo().getId(),
                        av.getUsuarioUid(),
                        av.getNota(),
                        av.getComentario(),
                        av.getDataCriacao()
                ))
                .collect(Collectors.toList());
    }

    public AvaliacaoResponseDTO editarAvaliacao(String id, AvaliacaoRequestDTO dto, Authentication auth) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        String usuarioAtual = auth.getName();

        boolean isDono = avaliacao.getUsuarioUid().equals(usuarioAtual);

        if (!isDono) {
            throw new SecurityException("Sem permissão para editar esta avaliação");
        }

        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        Avaliacao atualizada = avaliacaoRepository.save(avaliacao);

        return new AvaliacaoResponseDTO(
                atualizada.getId(),
                atualizada.getAbrigo().getId(),
                atualizada.getUsuarioUid(),
                atualizada.getNota(),
                atualizada.getComentario(),
                atualizada.getDataCriacao()
        );
    }

    public List<AvaliacaoResponseDTO> listarPorUsuario(Authentication auth) {
        String usuarioId = auth.getName();
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByUsuarioUid(usuarioId);

        return avaliacoes.stream()
                .map(av -> new AvaliacaoResponseDTO(
                        av.getId(),
                        av.getAbrigo() != null ? av.getAbrigo().getId() : null,
                        av.getUsuarioUid(),
                        av.getNota(),
                        av.getComentario(),
                        av.getDataCriacao()
                ))
                .collect(Collectors.toList());
    }


    public void deletarAvaliacao(String id, Authentication auth) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        String usuarioAtual = auth.getName();

        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        boolean isDono = avaliacao.getUsuarioUid().equals(usuarioAtual);

        if (!isAdmin && !isDono) {
            throw new SecurityException("Sem permissão para excluir esta avaliação");
        }

        avaliacaoRepository.deleteById(id);
    }
}
