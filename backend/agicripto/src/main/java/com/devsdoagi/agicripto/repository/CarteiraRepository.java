package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.AtivosCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsdoagi.agicripto.model.Carteira;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Integer> {}
