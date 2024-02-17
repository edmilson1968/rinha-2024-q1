package br.com.edm.app.rinha.model;

public record TransacaoClienteRequest(
        String valor,
        String tipo,
        String descricao
) { }
