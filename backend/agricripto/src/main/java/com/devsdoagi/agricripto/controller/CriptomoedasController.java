package com.devsdoagi.agricripto.controller;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.service.CriptomoedasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public List<Criptomoedas> getAll() {
        return criptomoedasService.findAll();
    }

    // Endpoint para buscar uma criptomoeda por ID.
    // URL: GET /api/criptomoedas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Criptomoedas> getById(@PathVariable Integer id) {
        Optional<Criptomoedas> cripto = criptomoedasService.findById(id);
        return cripto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Criptomoedas> createCripto(@RequestBody Criptomoedas cripto) {
        // O service vai salvar a nova criptomoeda no banco de dados
        Criptomoedas newCripto = criptomoedasService.save(cripto);
        // Retorna a nova criptomoeda com o status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(newCripto);
    }
}
