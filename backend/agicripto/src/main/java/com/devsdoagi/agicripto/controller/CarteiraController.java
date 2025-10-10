package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.DTO.PortfolioResponseDTO;
import com.devsdoagi.agicripto.service.CarteiraService;
import com.devsdoagi.agicripto.service.UsuariosService;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carteira")
public class CarteiraController {

    private final CarteiraService carteiraService;
    private final UsuariosService usuariosService;

    public CarteiraController(CarteiraService carteiraService, UsuariosService usuariosService) {

        this.carteiraService = carteiraService;
        this.usuariosService = usuariosService;

    }

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioResponseDTO> obterPortfolioCliente(HttpSession session) {

        Integer userId = usuariosService.checarSessaoEObterIdUsuario(session);
        return ResponseEntity.ok(carteiraService.obterPortfolioCliente(userId));

    }

}
