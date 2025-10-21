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

    // ===================== LISTAR TODAS AS CRIPTOMOEDAS =====================
    @GetMapping
    public List<CriptomoedasResponseDTO> getAll() {
        // Chama o novo metodo do Service que já converte para DTO
        return criptomoedasService.findAllDto();
    }
    // ===================== BUSCAR CRIPTOMOEDA ESPECÍFICA PELO ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<CriptomoedasResponseDTO> getById(@PathVariable Integer id) {
        Optional<Criptomoedas> cripto = criptomoedasService.findById(id);

        return cripto.map(CriptomoedasResponseDTO::new).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    // ===================== CADASTRAR NOVA CRIPTOMOEDA =====================
    @PostMapping
    public ResponseEntity<CriptomoedasResponseDTO> cadastrarCriptomoeda(@RequestBody CriptomoedasRequestDTO request) {
        // Chama o Service, enviando o DTO que contém o id_responsavel
        CriptomoedasResponseDTO response = criptomoedasService.cadastrar(request);

        // Retorna a resposta DTO com status 201 CREATED
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    // ===================== DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCriptomoeda(@PathVariable Integer id) {
        boolean removido = criptomoedasService.deletarPorId(id);
        if (removido) {
            return ResponseEntity.noContent().build(); // 204 OK sem corpo
        } else {
            return ResponseEntity.notFound().build(); // 404 se o id não existir
        }
    }
    // ===================== ATUALIZAR CRIPTOMOEDA  ======================
    @PutMapping("/{id}")
    public ResponseEntity<CriptomoedasResponseDTO> atualizarCriptomoeda(
            @PathVariable Integer id,
            @RequestBody CriptomoedasRequestDTO request) {
        CriptomoedasResponseDTO atualizada = criptomoedasService.atualizar(id, request);
        return ResponseEntity.ok(atualizada);
    }
}




