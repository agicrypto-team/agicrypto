package com.devsdoagi.agicripto.exception.historicoCriptomoedas;

public class HistoricoCriptomoedaNaoEncontradoException extends RuntimeException {
    public HistoricoCriptomoedaNaoEncontradoException(Integer id)
    {
        super("Histórico da criptomoeda com id (" + id + ") não encontrado.");
    }
}
