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

import java.math.BigDecimal;
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
        ativo.setValorTotalComprado(dto.getValorTotalComprado());

        // Carrega e Seta as Entidades relacionadas
        Carteira carteira = carteiraRepository.findById(dto.getIdCarteira()) // 'I' minúsculo
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteira não encontrada."));

        Criptomoedas cripto = criptomoedaRepository.findById(dto.getIdCriptomoeda()) // 'I' minúsculo
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criptomoeda não encontrada."));

        ativo.setCarteira(carteira);
        ativo.setCriptomoedas(cripto);

        // Salva e retorna o DTO de Resposta
        AtivosCarteira ativoSalvo = ativoRepository.save(ativo);
        return new AtivosCarteiraResponseDTO(ativoSalvo);
    }

    //METODO PARA ATUALIZAR ATIVOS
    public AtivosCarteira atualizar(
            Carteira carteira,
            Criptomoedas criptomoeda,
            BigDecimal quantidade,
            BigDecimal valorTotal,
            boolean isCompra) {

        // Busca se o ativo já existe na carteira
        AtivosCarteira ativo = ativoRepository
                .findByCarteiraAndCriptomoedas(carteira, criptomoeda)
                .orElse(null);

        if (ativo == null) {
            // Se não existe, cria um novo ativo (apenas em caso de compra)
            if (isCompra) {
                ativo = new AtivosCarteira();
                ativo.setCarteira(carteira);
                ativo.setCriptomoedas(criptomoeda);
                ativo.setQuantidade(quantidade);
                ativo.setValorTotalComprado(valorTotal);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Não é possível vender um ativo que não existe na carteira.");
            }
        } else {
            // Atualiza conforme o tipo da operação
            if (isCompra) {
                ativo.setQuantidade(ativo.getQuantidade().add(quantidade));
                ativo.setValorTotalComprado(ativo.getValorTotalComprado().add(valorTotal));
            } else {
                BigDecimal novaQuantidade = ativo.getQuantidade().subtract(quantidade);
                BigDecimal novoValorTotal = ativo.getValorTotalComprado().subtract(valorTotal);

                if (novaQuantidade.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Quantidade insuficiente para venda.");
                }

                ativo.setQuantidade(novaQuantidade);
                ativo.setValorTotalComprado(novoValorTotal);
            }
        }

        return ativoRepository.save(ativo);
    }

    public void deletar(Integer id) {
        ativoRepository.deleteById(id);
    }
}
