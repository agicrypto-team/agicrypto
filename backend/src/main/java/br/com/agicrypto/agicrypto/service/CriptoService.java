package br.com.agicrypto.agicrypto.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CriptoService {

    /*
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getBitcoinPrice() {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd,brl";
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getCoinPrice(String coin) {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + coin + "&vs_currencies=usd,brl";
        return restTemplate.getForObject(url, Map.class);
    }

     */
}
