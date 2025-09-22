package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.model.Transacoes;
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

    // Listar todas
    @GetMapping("/listar")
    public List<Transacoes> listar() {
        return transacoesService.listarTodas();
    }

    // Listar transações de um usuário
    @GetMapping("/usuario/{idUsuario}")
    public List<Transacoes> listarPorUsuario(@PathVariable Integer idUsuario) {
        return transacoesService.listarPorUsuario(idUsuario);
    }

    // Detalhar transação por ID
    @GetMapping("/{id}")
    public Optional<Transacoes> detalhar(@PathVariable Integer id) {
        return transacoesService.buscarPorId(id);
    }

    // Cadastrar nova transação
    @PostMapping("/cadastrar")
    public Transacoes cadastrar(@RequestBody Transacoes transacao) {
        return transacoesService.salvar(transacao);
    }
}
