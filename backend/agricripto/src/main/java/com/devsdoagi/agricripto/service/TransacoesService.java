package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.model.Transacoes;
import com.devsdoagi.agricripto.repository.TransacoesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransacoesService {

    private final TransacoesRepository transacoesRepository;

    public TransacoesService(TransacoesRepository transacoesRepository) {
        this.transacoesRepository = transacoesRepository;
    }

    // Listar todas
    public List<Transacoes> listarTodas() {
        return transacoesRepository.findAll();
    }

    // Listar transações de um usuário específico
    public List<Transacoes> listarPorUsuario(Integer idUsuario) {
        return transacoesRepository.findByUsuariosId(idUsuario);
    }

    // Buscar transação por ID
    public Optional<Transacoes> buscarPorId(Integer id) {
        return transacoesRepository.findById(id);
    }

    // Cadastrar nova transação
    public Transacoes salvar(Transacoes transacao) {
        return transacoesRepository.save(transacao);
    }
}
