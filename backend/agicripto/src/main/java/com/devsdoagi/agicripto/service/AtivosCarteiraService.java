package com.devsdoagi.agicripto.service;
import com.devsdoagi.agicripto.repository.AtivosCarteiraRepository;
import com.devsdoagi.agicripto.model.AtivosCarteira;
import com.devsdoagi.agicripto.repository.CarteiraRepository;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraRequestDTO;
import com.devsdoagi.agicripto.DTO.AtivosCarteiraResponseDTO;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.model.Carteira;
import com.devsdoagi.agicripto.model.AtivosCarteira;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;


@Service
public class AtivosCarteiraService {
    private final AtivosCarteiraRepository ativoRepository;
    private final CarteiraRepository carteiraRepository;
    private final CriptomoedasRepository criptomoedaRepository;

    // Injeção de dependência via construtor
    @Autowired
    public AtivosCarteiraService(AtivosCarteiraRepository ativoRepository, CarteiraRepository carteiraRepository,
                                 CriptomoedasRepository criptomoedaRepository) {
        this.ativoRepository = ativoRepository;
        this.carteiraRepository = carteiraRepository;
        this.criptomoedaRepository = criptomoedaRepository;
    }

    // Mapeamento Entity -> ResponseDTO usando referência de metodo para o construtor
    public List<AtivosCarteiraResponseDTO> listarTodos() {
        return ativoRepository.findAll().stream()
                .map(AtivosCarteiraResponseDTO::new) // Referência ao construtor
                .collect(Collectors.toList());
    }

    // Mapeamento Entity -> ResponseDTO
    public AtivosCarteiraResponseDTO buscarPorId(Integer id) {
        AtivosCarteira ativo = ativoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado."));
        return new AtivosCarteiraResponseDTO(ativo);
    }

    // Mapeamento RequestDTO -> Entity e salvamento
    public AtivosCarteiraResponseDTO criar(AtivosCarteiraRequestDTO dto) {

        // Mapeamento DTO para Entity
        AtivosCarteira ativo = new AtivosCarteira();
        ativo.setQuantidade(dto.getQuantidade());
        ativo.setPrecoMedio(dto.getPrecoMedio());

        // Carrega e Seta as Entidades relacionadas
        Carteira carteira = carteiraRepository.findById(dto.getIdCarteira()) // 'I' minúsculo
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteira não encontrada."));

        Criptomoedas cripto = criptomoedaRepository.findById(dto.getIdCriptomoeda()) // 'I' minúsculo
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criptomoeda não encontrada."));

        ativo.setCarteira(carteira);
        ativo.setCriptomoedas(cripto);
        ativo.setDataAtualizacao(LocalDateTime.now());

        // Salva e retorna o DTO de Resposta
        AtivosCarteira ativoSalvo = ativoRepository.save(ativo);
        return new AtivosCarteiraResponseDTO(ativoSalvo);
    }

    // Lógica
    public AtivosCarteiraResponseDTO atualizar(Integer id, AtivosCarteiraRequestDTO dto) {
        AtivosCarteira ativoExistente = ativoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ativo não encontrado para atualização."));

        // Atualiza os campos
        if (dto.getQuantidade() != null) {
            ativoExistente.setQuantidade(dto.getQuantidade());
        }
        if (dto.getPrecoMedio() != null) {
            ativoExistente.setPrecoMedio(dto.getPrecoMedio());
        }

        // Atualiza data
        ativoExistente.setDataAtualizacao(LocalDateTime.now());

        // Salva e retorna
        AtivosCarteira ativoAtualizado = ativoRepository.save(ativoExistente);
        return new AtivosCarteiraResponseDTO(ativoAtualizado);
    }

    public void deletar(Integer id) {
        ativoRepository.deleteById(id);
    }
}
