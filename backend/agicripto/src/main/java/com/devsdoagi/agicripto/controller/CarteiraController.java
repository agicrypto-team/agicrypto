package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.model.Carteira;
import com.devsdoagi.agicripto.model.Usuarios;
import com.devsdoagi.agicripto.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carteira")
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @PostMapping("/add")
    public ResponseEntity<Carteira> criarCarteira(@RequestBody Usuarios usuarios) {
        Carteira carteira = carteiraService.CriarCarteira(usuarios);
        return ResponseEntity.status(HttpStatus.CREATED).body(carteira);
    }
}
