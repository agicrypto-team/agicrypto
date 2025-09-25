package com.devsdoagi.agricripto.service;
import com.devsdoagi.agricripto.model.Criptomoedas;
import com.devsdoagi.agricripto.repository.CriptomoedasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service
public class CriptomoedasService {
    @Autowired
    private CriptomoedasRepository criptomoedasRepository;

    // Busca todas as criptomoedas.
    public List<Criptomoedas> findAll() {
        return criptomoedasRepository.findAll();
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

    // Metodo para salvar ou atualizar uma criptomoeda.
    public Criptomoedas save(Criptomoedas criptomoedas) {
        // Antes de salvar, definimos a data e hora atuais.
        // Isso resolve o problema de a coluna ser nula.
        criptomoedas.setMomentoCadastro(LocalDateTime.now());

        return criptomoedasRepository.save(criptomoedas);
    }
}
