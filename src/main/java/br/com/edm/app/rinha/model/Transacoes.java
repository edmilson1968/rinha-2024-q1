package br.com.edm.app.rinha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Arrays;

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

    public void validarTransacao() {
        if (valor == null ||
                valor <= 0 ||
                descricao == null ||
                descricao.isEmpty() ||
                descricao.length() > 10 ||
                !Arrays.asList("c", "d").contains(tipo)
        ) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public static record TransacaoClienteRequest(
            @Pattern(regexp = "[0-9]+")
            String valor,
            @Pattern(regexp = "[cd]")
            String tipo,
            String descricao) {};

    public static Transacoes transforma(TransacaoClienteRequest req) {
        return new Transacoes(null, Integer.valueOf(req.valor), req.tipo, req.descricao, null);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Transacoes{");
        sb.append("id=").append(id);
        sb.append(", idClientes=").append(idClientes);
        sb.append(", valor=").append(valor);
        sb.append(", tipo='").append(tipo).append('\'');
        sb.append(", descricao='").append(descricao).append('\'');
        sb.append(", realizadaEm=").append(realizadaEm);
        sb.append('}');
        return sb.toString();
    }
}
