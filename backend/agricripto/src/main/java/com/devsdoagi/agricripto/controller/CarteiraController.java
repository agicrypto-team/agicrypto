package com.devsdoagi.agricripto.controller;

import com.devsdoagi.agricripto.model.Carteira;
import com.devsdoagi.agricripto.model.Usuarios;
import com.devsdoagi.agricripto.service.CarteiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carteira")

public class CarteiraController {
    @Autowired
    private CarteiraService carteiraService;

    @PostMapping("/add")
    public ResponseEntity<Carteira> criarCarteira(@RequestBody Usuarios usuarios){
        Carteira carteira = carteiraService.CriarCarteira(usuarios);

        return ResponseEntity.status(HttpStatus.CREATED).body(carteira);

    }



}
