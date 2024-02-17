package br.com.edm.app.rinha.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record SaldoResponse(
        Integer total,
        @JsonProperty("data_extrato") LocalDateTime dataExtrato,
        Integer limite) {
}
