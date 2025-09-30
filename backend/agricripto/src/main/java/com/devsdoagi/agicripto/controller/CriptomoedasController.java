package com.devsdoagi.agicripto.controller;
import com.devsdoagi.agicripto.DTO.CriptomoedasRequestDTO;
import com.devsdoagi.agicripto.DTO.CriptomoedasResponseDTO;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.service.CriptomoedasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Define a URL base para todos os endpoints desta classe.
@RequestMapping("/api/criptomoedas")

public class CriptomoedasController {
    @Autowired
    private CriptomoedasService criptomoedasService;

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
    @PostMapping
    public ResponseEntity<CriptomoedasResponseDTO> create(@RequestBody CriptomoedasRequestDTO request) {
        // Chama o Service que agora recebe o DTO de Request
        CriptomoedasResponseDTO response = criptomoedasService.create(request);

        return ResponseEntity.ok(response);

    }

}
