package br.com.agicrypto.agicrypto.controller;

import br.com.agicrypto.agicrypto.service.CoinLibService;
import br.com.agicrypto.agicrypto.service.CoinLoreService;
import br.com.agicrypto.agicrypto.service.CriptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class CriptoController {

    // private final CoinLibService service;
    private final CoinLoreService service;


    public CriptoController(CoinLoreService service){
        this.service = service;
    }

    // Endpoint local seguro, acessível por HTTP
    @GetMapping("/bitcoin")
    public ResponseEntity<Object> getBitcoinPrice() {
        try {
            Object result = service.getBitcoinPrice();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(502).body(
                    Map.of("error", "Não foi possível conectar à API externa", "details", e.getMessage())
            );
        }
    }

    /*
    private final CriptoService criptoService;

    public CriptoController(CriptoService criptoService){
        this.criptoService = criptoService;
    }

    @GetMapping("/bitcoin")
    public Map<String, Object> getBitcoin() {
        return criptoService.getBitcoinPrice();
    }

    @GetMapping("/cripto/{coin}")
    public Map<String, Object> getAnyCriptoPrice(@PathVariable String coin) {
        return criptoService.getCoinPrice(coin);
    }

    */

}
