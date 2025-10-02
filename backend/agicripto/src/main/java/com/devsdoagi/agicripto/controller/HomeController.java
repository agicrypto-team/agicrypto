package com.devsdoagi.agicripto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html"; // redireciona para o index dentro de /static
    }
}

