package com.devsdoagi.agicripto.service;

import com.devsdoagi.agicripto.DTO.CriptomoedasRequestDTO;
import com.devsdoagi.agicripto.DTO.CriptomoedasResponseDTO;
import com.devsdoagi.agicripto.model.Criptomoedas;
import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.repository.CriptomoedasRepository;
import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import com.devsdoagi.agicripto.repository.UsuariosRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CriptomoedasService {
    @Autowired
    private CriptomoedasRepository criptomoedasRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private HistoricoCriptomoedasRepository historicoCriptomoedasRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Construtor: O Spring faz a injeção automaticamente aqui.
    public CriptomoedasService(CriptomoedasRepository criptomoedasRepository, UsuariosRepository usuariosRepository) {
        this.criptomoedasRepository = criptomoedasRepository;
        this.usuariosRepository = usuariosRepository;
    }

    // Busca todas as criptomoedas.
    public List<CriptomoedasResponseDTO> findAllDto() {
        // 1. Busca todas as entidades Criptomoedas no banco
        List<Criptomoedas> criptos = criptomoedasRepository.findAll();

        // 2. Converte a lista de entidades para a lista de DTOs
        // O 'return' e o 'Collectors' foram adicionados/corrigidos aqui.
        return criptos.stream()
                .map(CriptomoedasResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Busca uma criptomoeda pelo ID.
    public Optional<Criptomoedas> findById(Integer id) {
        return criptomoedasRepository.findById(id);
    }

    // O Spring injeta o valor da propriedade aqui.
    @Value("${api.coingecko.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Metodo para buscar os detalhes de uma criptomoeda.
    public String getCryptoDetails(String cryptoId) {
        // Concatenamos a URL base com o endpoint específico.
        String url = baseUrl + "coins/" + cryptoId;

        // Fazemos a requisição HTTP.
        // Se a API retornar um status de erro (ex: 404), o RestTemplate lança uma RestClientException
        return restTemplate.getForObject(url, String.class);
    }

    @Transactional // Garante que a criação da Criptomoeda e do Historico sejam atômicas.
    public CriptomoedasResponseDTO cadastrar(CriptomoedasRequestDTO request) {
        if (request.id_responsavel() == null) {
            throw new IllegalArgumentException("id_responsavel não pode ser nulo");
        }

        Usuarios responsavel = usuariosRepository.findById(request.id_responsavel())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Criptomoedas criptomoeda = new Criptomoedas();

        if (responsavel.getTipo().equalsIgnoreCase("Admin")){

            criptomoeda.setNome(request.nome());
            criptomoeda.setSigla(request.sigla());
            criptomoeda.setIcone(request.icone());
            criptomoeda.setUsuarios(responsavel);
            criptomoeda.setMomentoCadastro(LocalDateTime.now());

            criptomoeda = criptomoedasRepository.save(criptomoeda);

            salvarCotacaoInicial(criptomoeda);


        }

        return new CriptomoedasResponseDTO(criptomoeda);
    }

    private void salvarCotacaoInicial(Criptomoedas criptomoeda) {
        String nomeParaBusca = criptomoeda.getNome().toLowerCase().replace("\\s+", "-");

        try {
            String jsonResponse = getCryptoDetails(nomeParaBusca);
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode usdPriceNode = rootNode
                    .path("market_data")
                    .path("current_price")
                    .path("usd");

            BigDecimal cotacaoUsd = null;
            if (usdPriceNode.isNumber()) {
                cotacaoUsd = usdPriceNode.decimalValue();
            }

            if (cotacaoUsd == null) {
                System.err.println("AVISO: Cotação USD não encontrada na resposta da API para: " + criptomoeda.getNome());
                return;
            }

            String urlExchange = baseUrl + "exchange_rates";
            String exchangeResponse = restTemplate.getForObject(urlExchange, String.class);
            JsonNode exchangeRoot = objectMapper.readTree(exchangeResponse);

            JsonNode brlRateNode = exchangeRoot
                    .path("rates")
                    .path("brl")
                    .path("value");

            BigDecimal taxaCambio = null;
            if (brlRateNode.isNumber()) {
                taxaCambio = brlRateNode.decimalValue();
            }

            if (taxaCambio == null) {
                System.err.println("AVISO: Taxa de câmbio BRL não encontrada. Salvando em USD como fallback.");
                taxaCambio = BigDecimal.ONE;
            }

            BigDecimal cotacaoBrl = cotacaoUsd.multiply(taxaCambio);

            HistoricoCriptomoedas historico = new HistoricoCriptomoedas();
            historico.setCriptomoedas(criptomoeda);
            historico.setCotacao_momento(cotacaoBrl);
            historico.setMomento(LocalDateTime.now());

            historicoCriptomoedasRepository.save(historico);

            System.out.println("✅ Cotação inicial salva com sucesso em BRL para: "
                    + criptomoeda.getNome() + " | Valor: R$" + cotacaoBrl);

        } catch (RestClientException e) {
            System.err.println("ERRO: Falha ao chamar a API CoinGecko para "
                    + criptomoeda.getNome() + ". Mensagem: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("ERRO: Falha ao processar a resposta da API para "
                    + criptomoeda.getNome() + ". Mensagem: " + e.getMessage());
        }
    }


//    public CriptomoedasResponseDTO create(CriptomoedasRequestDTO request) {
//        if (request.id_responsavel() == null) {
//            throw new IllegalArgumentException("id_responsavel não pode ser nulo");
//        }
//
//        // Log para depuração
//        System.out.println("DEBUG - id_responsavel vindo do request: " + request.id_responsavel());
//
//        Usuarios responsavel = usuariosRepository.findById(request.id_responsavel())
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//
//        Criptomoedas criptomoeda = new Criptomoedas();
//        criptomoeda.setNome(request.nome());
//        criptomoeda.setSigla(request.sigla());
//        criptomoeda.setIcone(request.icone());
//        criptomoeda.setUsuarios(responsavel);
//        criptomoeda.setMomentoCadastro(LocalDateTime.now());
//
//        criptomoeda = criptomoedasRepository.save(criptomoeda);
//
//        return new CriptomoedasResponseDTO(criptomoeda);
//    }

}

