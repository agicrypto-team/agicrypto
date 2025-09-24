package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.DTO.TransacoesRequestDTO;
import com.devsdoagi.agricripto.DTO.TransacoesResponseDTO;
import com.devsdoagi.agricripto.service.TransacoesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public Optional<TransacoesResponseDTO> detalhar(@PathVariable Integer id) {
        return transacoesService.buscarPorId(id);
    }

    @PostMapping("/cadastrar")
    public TransacoesResponseDTO cadastrar(@RequestBody TransacoesRequestDTO dto) {
        return transacoesService.salvar(dto);
    }
}
