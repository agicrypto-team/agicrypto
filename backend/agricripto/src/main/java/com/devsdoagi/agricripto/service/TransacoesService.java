package com.devsdoagi.agricripto.service;

import com.devsdoagi.agricripto.DTO.TransacoesRequestDTO;
import com.devsdoagi.agricripto.DTO.TransacoesResponseDTO;
import com.devsdoagi.agricripto.model.Transacoes;
import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.repository.TransacoesRepository;
import com.devsdoagi.agricripto.repository.UsuariosRepository;
import com.devsdoagi.agricripto.repository.CriptomoedasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        dto.setMomento(t.getMomento());
        dto.setUsuarioId(t.getUsuarios().getId());
        dto.setUsuarioNome(t.getUsuarios().getNome()); // se disponível
        dto.setCriptomoedaId(t.getCriptomoeda().getId());
        dto.setCriptomoedaNome(t.getCriptomoeda().getNome()); // se disponível
        return dto;
    }

    // Listar todas
    public List<TransacoesResponseDTO> listarTodas() {
        return transacoesRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Listar por usuário
    public List<TransacoesResponseDTO> listarPorUsuario(Integer idUsuario) {
        return transacoesRepository.findByUsuariosId(idUsuario)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Buscar por ID
    public Optional<TransacoesResponseDTO> buscarPorId(Integer id) {
        return transacoesRepository.findById(id)
                .map(this::toResponseDTO);
    }

    // Salvar nova transação a partir de DTO
    public TransacoesResponseDTO salvar(TransacoesRequestDTO dto) {
        Usuarios usuario = usuariosRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Criptomoedas cripto = criptomoedasRepository.findById(dto.getCriptomoedaId())
                .orElseThrow(() -> new RuntimeException("Criptomoeda não encontrada"));

        Transacoes t = new Transacoes();
        t.setTipo(dto.getTipo());
        t.setValor(dto.getValor());
        t.setUsuarios(usuario);
        t.setCriptomoeda(cripto);

        Transacoes salvo = transacoesRepository.save(t);
        return toResponseDTO(salvo);
    }
}
