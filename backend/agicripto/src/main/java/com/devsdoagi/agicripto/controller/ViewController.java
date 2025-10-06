package com.devsdoagi.agicripto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String mostrarTelaIndex() {

        return "index";

    }

    @GetMapping("/login")
    public String mostrarTelaLogin() {

        return "auth/login";

    }

    @GetMapping("/home")
    public String mostrarTelaHome() {

        return "cliente/homeUsuario";

    }
}

