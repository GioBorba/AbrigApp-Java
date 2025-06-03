package com.fiap.globalsolution.api;

import com.fiap.globalsolution.dto.AbrigoResponseDTO;
import com.fiap.globalsolution.service.AbrigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/abrigos")
@CrossOrigin // permite chamadas do app mobile
public class AbrigosRestController {

    @Autowired
    private AbrigoService abrigoService;

    // GET /api/abrigos - lista todos os abrigos
    @GetMapping
    public List<AbrigoResponseDTO> listarTodos(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String cidade
    ) {
        return abrigoService.filtrar(estado, cidade);
    }

    // GET /api/abrigos/{id} - detalhes de um abrigo
    @GetMapping("/{id}")
    public AbrigoResponseDTO buscarPorId(@PathVariable String id) {
        return abrigoService.buscarPorId(id);
    }
}
