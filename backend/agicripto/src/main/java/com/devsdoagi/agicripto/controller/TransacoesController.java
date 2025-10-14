package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.TransacoesRequestDTO;
import com.devsdoagi.agicripto.DTO.TransacoesResponseDTO;
import com.devsdoagi.agicripto.model.Transacoes;
import com.devsdoagi.agicripto.service.TransacoesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacoesController {

    private final TransacoesService transacoesService;

    public TransacoesController(TransacoesService transacoesService) {
        this.transacoesService = transacoesService;
    }

    @GetMapping("/listar")
    public List<TransacoesResponseDTO> listar() {
        return transacoesService.listarTodas();
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<TransacoesResponseDTO> listarPorUsuario(@PathVariable Integer idUsuario) {
        return transacoesService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/{id}")
    public TransacoesResponseDTO detalhar(@PathVariable Integer id) {
        return transacoesService.buscarPorId(id);
    }

   // @PostMapping("/cadastrar")
   // public TransacoesResponseDTO cadastrar(@RequestBody TransacoesRequestDTO dto) {
   //     return transacoesService.salvar(dto);
 //   }

    @PostMapping
    public ResponseEntity<TransacoesResponseDTO> criarTransacao(@RequestBody TransacoesRequestDTO dto) {
        TransacoesResponseDTO transacao = transacoesService.salvar(dto);
        return ResponseEntity.ok(transacao);
    }
}
