package com.fiap.globalsolution.service;

import com.fiap.globalsolution.dto.AbrigoRequestDTO;
import com.fiap.globalsolution.dto.AbrigoResponseDTO;
import com.fiap.globalsolution.model.Abrigo;
import com.fiap.globalsolution.model.Role;
import com.fiap.globalsolution.rabbitmq.AbrigoProducer;
import com.fiap.globalsolution.repository.AbrigoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbrigoService {

    @Autowired
    private AbrigoRepository abrigoRepository;
    @Autowired
    private AbrigoProducer abrigoProducer;

    // Listar TODOS os abrigos
    public List<AbrigoResponseDTO> listarTodos() {
        return abrigoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar abrigo por ID
    public AbrigoResponseDTO buscarPorId(String id) {
        Abrigo abrigo = abrigoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abrigo não encontrado"));
        return toResponseDTO(abrigo);
    }

    //  Criar novo abrigo
    public AbrigoResponseDTO criar(AbrigoRequestDTO dto) {
        Abrigo abrigo = new Abrigo();
        abrigo.setNome(dto.nome());
        abrigo.setEndereco(dto.endereco());
        abrigo.setCidade(dto.cidade());
        abrigo.setEstado(dto.estado());
        abrigo.setLatitude(dto.latitude());
        abrigo.setLongitude(dto.longitude());
        abrigo.setAtivo(true); // Default: true (ativo)

        abrigo = abrigoRepository.save(abrigo);
        abrigoProducer.enviarMensagem("Novo abrigo registrado no endereço: " + abrigo.getEndereco());
        return toResponseDTO(abrigo);
    }

    // Atualizar abrigo
    public AbrigoResponseDTO atualizar(String id, AbrigoRequestDTO dto, Role role) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Apenas ADMIN pode atualizar abrigos");
        }

        Abrigo abrigo = abrigoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abrigo não encontrado"));

        // Atualiza campos permitidos
        abrigo.setNome(dto.nome());
        abrigo.setEndereco(dto.endereco());
        abrigo.setCidade(dto.cidade());
        abrigo.setEstado(dto.estado());
        abrigo.setLatitude(dto.latitude());
        abrigo.setLongitude(dto.longitude());

        abrigo = abrigoRepository.save(abrigo);
        return toResponseDTO(abrigo);
    }

    //  Deletar abrigo
    public void deletar(String id, Role role) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Apenas ADMIN pode excluir abrigos permanentemente");
        }

        if (!abrigoRepository.existsById(id)) {
            throw new RuntimeException("Abrigo não encontrado");
        }
        abrigoRepository.deleteById(id);
    }

    // Filtrar abrigo por cidade/estado
    public List<AbrigoResponseDTO> filtrar(String estado, String cidade) {
        boolean temEstado = StringUtils.hasText(estado);
        boolean temCidade = StringUtils.hasText(cidade);

        if (temEstado && temCidade) {
            return abrigoRepository.findByEstadoAndCidade(estado, cidade)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        } else if (temEstado) {
            return abrigoRepository.findByEstado(estado)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        } else if (temCidade) {
            return abrigoRepository.findByCidade(cidade)
                    .stream()
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
        } else {
            return listarTodos();
        }
    }


    private AbrigoResponseDTO toResponseDTO(Abrigo abrigo) {
        return new AbrigoResponseDTO(
                abrigo.getId(),
                abrigo.getNome(),
                abrigo.getEndereco(),
                abrigo.getCidade(),
                abrigo.getEstado(),
                abrigo.getLatitude(),
                abrigo.getLongitude(),
                abrigo.getAtivo()
        );
    }
}