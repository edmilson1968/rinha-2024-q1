package br.com.edm.app.rinha.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record TransacaoExtrato(
        Integer valor,
        String tipo,
        String descricao,
        @JsonProperty("realizada_em") LocalDateTime realizadaEm
) { }
