package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.model.Transacoes.Transacoes;
import com.devsdoagi.agricripto.model.Transacoes.TransacoesDTO;
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
    public List<TransacoesDTO> listar() {
        return transacoesService.listarTodas().stream().map(this::converterParaDTO).toList();
    }

    // Listar transações de um usuário
    @GetMapping("/usuario/{idUsuario}")
    public List<TransacoesDTO> listarPorUsuario(@PathVariable Integer idUsuario) {
        return transacoesService.listarPorUsuario(idUsuario).stream().map(this::converterParaDTO).toList();
    }

    // Detalhar transação por ID
    @GetMapping("/{id}")
    public TransacoesDTO detalhar(@PathVariable Integer id) {
        Optional<Transacoes> transacao = transacoesService.buscarPorId(id);
        return transacao.map(this::converterParaDTO).orElse(null);
    }

    // Cadastrar nova transação
    @PostMapping("/cadastrar")
    public TransacoesDTO cadastrar(@RequestBody Transacoes transacao) {
        Transacoes tSalva = transacoesService.salvar(transacao);
        return converterParaDTO(tSalva);
    }

    // Método privado para converter Transacoes → TransacoesDTO
    private TransacoesDTO converterParaDTO(Transacoes transacao) {
        TransacoesDTO dto = new TransacoesDTO();
        dto.setId(transacao.getId());
        dto.setTipo(transacao.getTipo());
        dto.setValor(transacao.getValor());
        dto.setMomento(transacao.getMomento());

        dto.setUsuarioId(transacao.getUsuarios().getId());
        dto.setUsuarioNome(transacao.getUsuarios().getNome());

        dto.setCriptomoedaId(transacao.getCriptomoeda().getId());
        dto.setCriptomoedaNome(transacao.getCriptomoeda().getNome());

        return dto;
    }
}
