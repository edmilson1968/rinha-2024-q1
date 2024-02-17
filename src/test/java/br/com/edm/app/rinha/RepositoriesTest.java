package br.com.edm.app.rinha;

import br.com.edm.app.rinha.model.Clientes;
import br.com.edm.app.rinha.model.TransacaoExtrato;
import br.com.edm.app.rinha.model.Transacoes;
import br.com.edm.app.rinha.repositories.ClientesRepository;
import br.com.edm.app.rinha.repositories.TransacoesRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RepositoriesTest {

    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    ClientesRepository clientesRepository;
    @Autowired
    TransacoesRepository transacoesRepository;

    @BeforeEach
    void setup() {
        jdbcClient.sql("update clientes set saldo = 0").update();
        jdbcClient.sql("delete from transacoes").update();

    }

    @Test
    void assertThatClientesHasAllRecords() {
        assertThat(JdbcTestUtils.countRowsInTable(jdbcClient, "clientes")).isEqualTo(5);
        //jdbcClient.sql("select * from clientes").query().listOfRows();
    }

    @Test
    void shouldFindClienteByIdEquals1() {
        final Optional<Clientes> cliente = clientesRepository.findById(1L);
        assertThat(cliente.isPresent()).isEqualTo(true);
        assertThat(cliente.get().getLimite()).isEqualTo(1000 * 100);
        assertThat(cliente.get().getSaldo()).isEqualTo(0);
    }
    @Test
    void shouldNotFindClienteByIdEquals6() {
        final Optional<Clientes> cliente = clientesRepository.findById(6L);
        assertThat(cliente.isPresent()).isEqualTo(false);
    }

    @Test
    void shouldUpdateClienteWithPositiveBalance() {
        final int qtd = clientesRepository.updateSaldoCliente(1L, 100);
        assertThat(qtd).isEqualTo(1);

        final Optional<Clientes> cliente = clientesRepository.findById(1L);
        assertThat(cliente.get().getSaldo()).isEqualTo(100);
    }

    @Test
    void shouldUpdateClienteWithNegativeBalance() {
        final int qtd = clientesRepository.updateSaldoCliente(2L, -100);
        assertThat(qtd).isEqualTo(1);

        final Optional<Clientes> cliente = clientesRepository.findById(2L);
        assertThat(cliente.get().getSaldo()).isEqualTo(-100);
    }

    @Test
    void shouldAddTransacaoToClient1() {
        final Transacoes transacoes = new Transacoes(1L, 100, "c", "credito", OffsetDateTime.now());
        transacoesRepository.addTransacao(transacoes);
        assertThat(JdbcTestUtils.countRowsInTable(jdbcClient, "transacoes")).isEqualTo(1);
    }

    @Test
    void shoutGetLast10Transacoes() {
        final Transacoes transacoes = new Transacoes(2L, 100, "c", "credito", OffsetDateTime.now());
        for (int i = 0; i < 12; i++) {
            transacoesRepository.addTransacao(transacoes);
        }
        final List<TransacaoExtrato> top10Transacoes = transacoesRepository.getTop10Transacoes(2L);
        assertThat(top10Transacoes).isNotEmpty();
        assertThat(top10Transacoes).hasSize(10);
        assertThat(top10Transacoes).hasOnlyElementsOfType(TransacaoExtrato.class);
    }
}
