package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.AtivosCarteiraRequestDTO;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraResponseDTO;
import com.devsdoagi.agicripto.DTO.AtivoVenderResponseDTO;
import com.devsdoagi.agicripto.service.AtivosCarteiraService;
import com.devsdoagi.agicripto.service.CarteiraService;
import com.devsdoagi.agicripto.service.UsuariosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@RestController
@RequestMapping("/api/ativos-carteira")
public class AtivosCarteiraController {

    private final AtivosCarteiraService service;
    private final CarteiraService carteiraService;
    private final UsuariosService usuariosService;

    public AtivosCarteiraController(AtivosCarteiraService service, CarteiraService carteiraService, UsuariosService usuariosService) {
        this.service = service;
        this.carteiraService = carteiraService;
        this.usuariosService = usuariosService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAtivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtivosCarteiraResponseDTO> atualizar(
            @PathVariable Integer id,
            @RequestBody AtivosCarteiraRequestDTO requestDTO) {
        AtivosCarteiraResponseDTO ativoAtualizado = service.atualizar(id, requestDTO);
        return ResponseEntity.ok(ativoAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Integer id) {
        service.deletar(id);
    }

    // ✅ NOVO ENDPOINT: lista apenas as criptomoedas que o usuário possui (para o botão "Vender")
    @GetMapping("/do-usuario")
    public ResponseEntity<List<AtivoVenderResponseDTO>> listarCriptomoedasDoUsuario(HttpSession session) {
        Integer userId = usuariosService.checarSessaoEObterIdUsuario(session);
        List<AtivoVenderResponseDTO> ativos = carteiraService.listarCriptomoedasUsuario(userId);
        return ResponseEntity.ok(ativos);
    }
}
