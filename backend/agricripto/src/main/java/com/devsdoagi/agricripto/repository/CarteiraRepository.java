package com.devsdoagi.agricripto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsdoagi.agricripto.model.Carteira;

@Repository
public interface CarteiraRepository extends JpaRepository<Carteira, Integer> {

}
