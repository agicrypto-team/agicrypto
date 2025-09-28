package com.devsdoagi.agricripto.service;
import com.devsdoagi.agricripto.DTO.CriptomoedasRequestDTO;
import com.devsdoagi.agricripto.DTO.CriptomoedasResponseDTO;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.repository.CriptomoedasRepository;
import com.devsdoagi.agricripto.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        return restTemplate.getForObject(url, String.class);
    }

    public CriptomoedasResponseDTO create(CriptomoedasRequestDTO request) {
        if (request.id_responsavel() == null) {
            throw new IllegalArgumentException("id_responsavel não pode ser nulo");
        }

        // Log para depuração
        System.out.println("DEBUG - id_responsavel vindo do request: " + request.id_responsavel());

        Usuarios responsavel = usuariosRepository.findById(request.id_responsavel())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Criptomoedas criptomoeda = new Criptomoedas();
        criptomoeda.setNome(request.nome());
        criptomoeda.setSigla(request.sigla());
        criptomoeda.setIcone(request.icone());
        criptomoeda.setUsuarios(responsavel);
        criptomoeda.setMomentoCadastro(LocalDateTime.now());

        criptomoeda = criptomoedasRepository.save(criptomoeda);

        return new CriptomoedasResponseDTO(criptomoeda);
    }

}

