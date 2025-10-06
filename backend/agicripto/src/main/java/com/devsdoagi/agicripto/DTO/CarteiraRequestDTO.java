package com.devsdoagi.agicripto.DTO;


public class CarteiraRequestDTO {

    private Integer idCliente;

    public CarteiraRequestDTO() {
    }
    public CarteiraRequestDTO(Integer idCliente) {
        this.idCliente = idCliente;
    }
    public Integer getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }
}