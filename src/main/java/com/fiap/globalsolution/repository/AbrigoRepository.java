package com.fiap.globalsolution.repository;

import com.fiap.globalsolution.model.Abrigo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AbrigoRepository extends MongoRepository<Abrigo, String> {
    List<Abrigo> findByEstado(String estado);
    List<Abrigo> findByCidade(String cidade);
    List<Abrigo> findByEstadoAndCidade(String estado, String cidade);

}
