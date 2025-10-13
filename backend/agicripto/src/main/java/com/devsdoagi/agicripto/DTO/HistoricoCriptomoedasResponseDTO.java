package com.devsdoagi.agicripto.DTO;

import com.devsdoagi.agicripto.model.HistoricoCriptomoedas;

import java.time.format.DateTimeFormatter;

public record HistoricoCriptomoedasResponseDTO(
        Integer idCriptomoeda,
        String nome,
        String sigla,
        Double cotacaoMomento,
        String momento
) {
    public HistoricoCriptomoedasResponseDTO(HistoricoCriptomoedas historico) {
        this(
                historico.getCriptomoedas().getId(),
                historico.getCriptomoedas().getNome(),
                historico.getCriptomoedas().getSigla(),
                historico.getCotacao_momento() != null ? historico.getCotacao_momento().doubleValue() : null,
                historico.getMomento() != null
                       ? historico.getMomento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null
        );
    }
}
