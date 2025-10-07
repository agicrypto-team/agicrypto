package com.devsdoagi.agicripto.repository;
import com.devsdoagi.agicripto.model.AtivosCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtivosCarteiraRepository extends JpaRepository<AtivosCarteira, Integer>{

}
