package br.com.agicrypto.agicrypto.controller;

import br.com.agicrypto.agicrypto.service.CriptoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CriptoController {
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

}
