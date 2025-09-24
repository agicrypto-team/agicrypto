package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.model.Transacoes.Transacoes;
import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.repository.TransacoesRepository;
import com.devsdoagi.agricripto.repository.UsuariosRepository;
import com.devsdoagi.agricripto.repository.CriptomoedasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransacoesService {

    private final TransacoesRepository transacoesRepository;
    private final UsuariosRepository usuariosRepository;
    private final CriptomoedasRepository criptomoedasRepository;

    public TransacoesService(TransacoesRepository transacoesRepository,
                             UsuariosRepository usuariosRepository,
                             CriptomoedasRepository criptomoedasRepository) {
        this.transacoesRepository = transacoesRepository;
        this.usuariosRepository = usuariosRepository;
        this.criptomoedasRepository = criptomoedasRepository;
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
        // Buscar entidades gerenciadas
        Usuarios usuario = usuariosRepository.findById(transacao.getUsuarios().getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Criptomoedas cripto = criptomoedasRepository.findById(transacao.getCriptomoeda().getId())
                .orElseThrow(() -> new RuntimeException("Criptomoeda não encontrada"));

        transacao.setUsuarios(usuario);
        transacao.setCriptomoeda(cripto);

        return transacoesRepository.save(transacao);
    }
}
