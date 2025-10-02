package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.DTO.TransacoesRequestDTO;
import com.devsdoagi.agicripto.DTO.TransacoesResponseDTO;
import com.devsdoagi.agicripto.model.Transacoes;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.repository.TransacoesRepository;
import com.devsdoagi.agicripto.repository.UsuariosRepository;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.exception.transacoes.TransacaoNaoEncontradaException;
import com.devsdoagi.agicripto.exception.transacoes.UsuarioNaoEncontradoException;
import com.devsdoagi.agicripto.exception.transacoes.CriptomoedaNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    // Converter Transacoes -> TransacoesResponseDTO
    private TransacoesResponseDTO toResponseDTO(Transacoes t) {
        TransacoesResponseDTO dto = new TransacoesResponseDTO();
        dto.setId(t.getId());
        dto.setTipo(t.getTipo());
        dto.setValor(t.getValor());
        dto.setQuantidadeCripto(t.getQuantidade_cripto());
        dto.setMomento(t.getMomento());
        dto.setUsuarioId(t.getUsuarios().getId());
        dto.setUsuarioNome(t.getUsuarios().getNome());
        dto.setCriptomoedaId(t.getCriptomoeda().getId());
        dto.setCriptomoedaNome(t.getCriptomoeda().getNome());
        return dto;
    }

    // Listar todas as transações
    public List<TransacoesResponseDTO> listarTodas() {
        return transacoesRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Listar transações por usuário
    public List<TransacoesResponseDTO> listarPorUsuario(Integer idUsuario) {
        usuariosRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(idUsuario));

        return transacoesRepository.findByUsuariosId(idUsuario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar transação por ID
    public TransacoesResponseDTO buscarPorId(Integer id) {
        Transacoes t = transacoesRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));
        return toResponseDTO(t);
    }

    // Salvar nova transação
    public TransacoesResponseDTO salvar(TransacoesRequestDTO dto) {
        Usuarios usuario = usuariosRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(dto.getUsuarioId()));
        Criptomoedas cripto = criptomoedasRepository.findById(dto.getCriptomoedaId())
                .orElseThrow(() -> new CriptomoedaNaoEncontradaException(dto.getCriptomoedaId()));

        Transacoes t = new Transacoes();
        t.setTipo(dto.getTipo());
        t.setValor(dto.getValor());
        t.setQuantidade_cripto(dto.getQuantidadeCripto());
        t.setUsuarios(usuario);
        t.setCriptomoeda(cripto);

        Transacoes salvo = transacoesRepository.save(t);
        return toResponseDTO(salvo);
    }
}
