package com.fiap.globalsolution.repository;

import com.fiap.globalsolution.model.Avaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {
    List<Avaliacao> findByAbrigoId(String abrigoId);
    List<Avaliacao> findByUsuarioUid(String usuarioUid);
}
