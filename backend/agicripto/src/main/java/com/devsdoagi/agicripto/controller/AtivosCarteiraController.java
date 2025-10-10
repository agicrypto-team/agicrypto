package com.devsdoagi.agicripto.controller;
import com.devsdoagi.agicripto.model.AtivosCarteira;
import com.devsdoagi.agicripto.service.AtivosCarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraResponseDTO;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/ativos-carteira")
public class AtivosCarteiraController {

    private final AtivosCarteiraService service;

    // Injeção de dependência via construtor
    //@Autowired
    public AtivosCarteiraController(AtivosCarteiraService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AtivosCarteiraResponseDTO>> listar() {
        List<AtivosCarteiraResponseDTO> ativos = service.listarTodos();
        return ResponseEntity.ok(ativos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtivosCarteiraResponseDTO> buscarPorId(@PathVariable Integer id) {
        AtivosCarteiraResponseDTO ativo = service.buscarPorId(id);
        return ResponseEntity.ok(ativo);
    }

    @PostMapping
    public ResponseEntity<AtivosCarteiraResponseDTO> criar(@RequestBody AtivosCarteiraRequestDTO requestDTO) {
        AtivosCarteiraResponseDTO novoAtivo = service.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtivo); // Código 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtivosCarteiraResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody AtivosCarteiraRequestDTO requestDTO) {

        AtivosCarteiraResponseDTO ativoAtualizado = service.atualizar(id, requestDTO);
        return ResponseEntity.ok(ativoAtualizado); // Código 200
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna o código 204
    public void deletar(@PathVariable Integer id) {
        service.deletar(id);
    }
}
