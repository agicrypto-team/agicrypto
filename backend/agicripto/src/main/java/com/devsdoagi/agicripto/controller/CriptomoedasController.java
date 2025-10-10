package com.devsdoagi.agicripto.controller;
import com.devsdoagi.agicripto.DTO.CriptomoedasRequestDTO;
import com.devsdoagi.agicripto.DTO.CriptomoedasResponseDTO;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.service.CriptomoedasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Define a URL base para todos os endpoints desta classe.
@RequestMapping("/api/criptomoedas")
@CrossOrigin(origins = "http://localhost:8080/pages/admin/homeAdmin.html")
public class CriptomoedasController {
    @Autowired
    private CriptomoedasService criptomoedasService;
    private CriptomoedasRepository criptomoedasRepository;

    // Endpoint para buscar todas as criptomoedas.
    // URL: GET /api/criptomoedas
    @GetMapping
    public List<CriptomoedasResponseDTO> getAll() {
        // Chama o novo metodo do Service que já converte para DTO
        return criptomoedasService.findAllDto();
    }
    // Endpoint para buscar uma criptomoeda por ID.
    // URL: GET /api/criptomoedas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CriptomoedasResponseDTO> getById(@PathVariable Integer id) {
        Optional<Criptomoedas> cripto = criptomoedasService.findById(id);

        return cripto.map(CriptomoedasResponseDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /*@PostMapping
    public ResponseEntity<CriptomoedasResponseDTO> create(@RequestBody CriptomoedasRequestDTO request) {
        // Chama o Service que agora recebe o DTO de Request
        CriptomoedasResponseDTO response = criptomoedasService.cadastrar(request);
        return ResponseEntity.ok(response);
    }*/
    // Endpoint POST para cadastrar
    @PostMapping
    public ResponseEntity<CriptomoedasResponseDTO> cadastrarCriptomoeda(@RequestBody CriptomoedasRequestDTO request) {
        // 1. Chama o Service, enviando o DTO que contém o id_responsavel
        CriptomoedasResponseDTO response = criptomoedasService.cadastrar(request);

        // 2. Retorna a resposta DTO com status 201 CREATED
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}




