package br.com.edm.app.rinha.repositories;

import br.com.edm.app.rinha.model.Clientes;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
    public int updateSaldoCliente(Long id, Integer valor) {
        return jdbcClient.sql("""
                UPDATE clientes SET
                  saldo = saldo + :valor
                WHERE id = :id
                """)
                .param("valor", valor)
                .param("id", id)
                .update();
    }
}
