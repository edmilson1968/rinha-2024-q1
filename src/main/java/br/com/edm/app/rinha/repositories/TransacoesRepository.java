package br.com.edm.app.rinha.repositories;

import br.com.edm.app.rinha.model.TransacaoExtrato;
import br.com.edm.app.rinha.model.Transacoes;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransacoesRepository {
    private final JdbcClient jdbcClient;

    public TransacoesRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void addTransacao(Transacoes transacao) {
        jdbcClient.sql("""
                INSERT INTO transacoes (
                    id_clientes
                  , valor
                  , tipo
                  , descricao
                  , realizada_em
                )
                VALUES (
                    :id_clientes
                  , :valor
                  , :tipo
                  , :descricao
                  , :realizada_em
                )
                """)
                .param("id_clientes", transacao.getIdClientes())
                .param("valor", transacao.getValor())
                .param("tipo", transacao.getTipo())
                .param("descricao", transacao.getDescricao())
                .param("realizada_em", transacao.getRealizadaEm())
                .update();
    }

    public List<TransacaoExtrato> getTop10Transacoes(Long idCliente) {
        return jdbcClient.sql(
                    "SELECT * FROM transacoes WHERE id_clientes = :id_clientes ORDER BY realizada_em DESC LIMIT 10"
                )
                .param("id_clientes", idCliente)
                .query(TransacaoExtrato.class)
                .list();
    }
}
