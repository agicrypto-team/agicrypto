package br.com.agicrypto.agicrypto.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoinLoreService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Object getBitcoinPrice() {
        String url = "https://api.coinlore.net/api/tickers/?id=90";
        return restTemplate.getForObject(url, Object.class);
    }
}
