package br.com.edm.app.rinha.repositories;

import br.com.edm.app.rinha.model.Clientes;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Optional;

//@Repository
@Component
public class ClientesRepository {

    private final JdbcClient jdbcClient;
    public ClientesRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Clientes> findById(Long id) {
        return jdbcClient
                .sql("""
                select * from clientes where id = :id
                """)
                .param("id", id)
                .query(Clientes.class)
                .optional();
    }
    public Optional<Clientes> atualizaSaldoCliente(Long id, Integer valor) {
        return jdbcClient.sql("""
                UPDATE clientes SET
                  saldo = saldo + :valor
                WHERE id = :id
                RETURNING id, saldo, limite;
                """)
                .param("valor", valor)
                .param("id", id)
                .query(Clientes.class)
                .optional()
                ;
    }
}
