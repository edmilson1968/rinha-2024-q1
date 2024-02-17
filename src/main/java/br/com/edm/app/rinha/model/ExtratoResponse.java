package br.com.edm.app.rinha.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExtratoResponse(
    SaldoResponse saldo,
    @JsonProperty("ultimas_transacoes") List<TransacaoExtrato> ultimasTransacoes
) { }

