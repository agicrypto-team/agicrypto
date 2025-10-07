package com.devsdoagi.agicripto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    /* Utilizando 'forward:/pages/...'  não dependemos do Thymeleaf e evitamos erro ao tentar renderizar */

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String mostrarTelaLogin() {
        return "forward:/pages/auth/login.html";
    }

    @GetMapping("/home")
    public String mostrarTelaHome() {
        return "forward:/pages/cliente/homeUsuario.html";
    }

    @GetMapping("/admin")
    public String mostrarTelaAdmin() {
        return "forward:/pages/admin/homeAdmin.html";
    }
}

