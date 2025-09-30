package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoCriptomoedasRepository extends JpaRepository<HistoricoCriptomoedas, Integer> {
//    List<HistoricoCriptomoedas> encontrarPorCriptomoedasID(Integer idCriptomoeda);
}
