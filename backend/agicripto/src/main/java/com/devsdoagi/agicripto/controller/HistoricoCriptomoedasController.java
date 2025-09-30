package com.devsdoagi.agicripto.controller;

import com.devsdoagi.agicripto.repository.HistoricoCriptomoedasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoCriptomoedasController {

    private final HistoricoCriptomoedasRepository historicoCriptomoedasRepository;

}
