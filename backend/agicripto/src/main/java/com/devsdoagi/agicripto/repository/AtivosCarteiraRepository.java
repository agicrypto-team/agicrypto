package com.devsdoagi.agicripto.repository;

import com.devsdoagi.agicripto.model.AtivosCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtivosCarteiraRepository extends JpaRepository<AtivosCarteira, Integer>{

    List<AtivosCarteira> findByCarteira_Usuarios_Id(Integer userId);

}
