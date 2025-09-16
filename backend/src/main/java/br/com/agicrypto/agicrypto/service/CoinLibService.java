package br.com.agicrypto.agicrypto.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoinLibService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Object getBitcoinPrice() {
        String url = "https://coinlib.io/api/v1/coin?key=demo&symbol=BTC";
        return  restTemplate.getForObject(url, Object.class);
    }
}
