package br.com.edm.app.rinha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class Transacoes {

    @JsonIgnore
    private Long id;
    private Long idClientes;
    private Integer valor;
    private String tipo;
    private String descricao;
    @JsonProperty("realizada_em")
    private OffsetDateTime realizadaEm;

    public Transacoes(Long idClientes, Integer valor, String tipo, String descricao, OffsetDateTime realizadaEm) {
        this.idClientes = idClientes;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.realizadaEm = realizadaEm;
    }

    public Long getIdClientes() {
        return idClientes;
    }

    public void setIdClientes(Long idClientes) {
        this.idClientes = idClientes;
    }

    public Integer getValor() {
        return valor;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public OffsetDateTime getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(OffsetDateTime realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public static Transacoes transforma(TransacaoClienteRequest req) {
        return new Transacoes(null, Integer.valueOf(req.valor()), req.tipo(), req.descricao(), null);
    }

}
