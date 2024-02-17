package br.com.edm.app.rinha;

import br.com.edm.app.rinha.model.*;
import br.com.edm.app.rinha.service.ClientesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ClienteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ClientesService clientesService;

    @Test
    void shouldGetExtratoCliente() throws Exception {
        final SaldoResponse saldoResponse = new SaldoResponse(1, LocalDateTime.now(), 10000);
        final List<TransacaoExtrato> ultimasTransacoes = List.of(
                new TransacaoExtrato(2, "c", "credito", LocalDateTime.now()),
                new TransacaoExtrato(1, "d", "debito", LocalDateTime.now())
        );
        final ExtratoResponse extratoResponse = new ExtratoResponse(saldoResponse, ultimasTransacoes);
        when(clientesService.handleExtratoResponse(1L)).thenReturn(extratoResponse);
        mockMvc.perform(get("/clientes/{id}/extrato", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo", notNullValue()))
                .andExpect(jsonPath("$.saldo.total", is(1)))
                .andExpect(jsonPath("$.saldo.limite", is(10000)))
                .andExpect(jsonPath("$.saldo.data_extrato", matchesRegex("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d\\.\\d+")))

        ;

    }

    @Test
    void shouldThrowsNotFoundExceptionOnGetExtrato() throws Exception {
        when(clientesService.handleExtratoResponse(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/clientes/{id}/extrato", 1L)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"valor\": 1.2, \"tipo\": \"d\", \"descricao\": \"devolve\"}",
            "{\"valor\": 1, \"tipo\": \"x\", \"descricao\": \"devolve\"}",
            "{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"123456789 e mais um pouco\"}",
            "{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"\"}",
            "{\"valor\": 1, \"tipo\": \"c\", \"descricao\": null}"
    })
    void shouldThrowsUnprocessableEntityExceptionOnAddTransaction(String body) throws Exception {
        mockMvc.perform(post("/clientes/{id}/transacoes", 1L)
                .contentType("application/json")
                .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldThrowNotFoundExceptionOnAddTransaction() throws Exception {
        when(
                clientesService.handleTransacao(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Transacoes.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(post("/clientes/{id}/transacoes", 1L)
                        .contentType("application/json")
                        .content("{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"toma\"}"))
                .andExpect(status().isNotFound());

    }

    @Test
    void shouldAddTransaction() throws Exception {
        final TransacaoClienteResponse transacaoClienteResponse = new TransacaoClienteResponse(100000, 1);
        when(clientesService.handleTransacao(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(Transacoes.class)))
                .thenReturn(transacaoClienteResponse);

        mockMvc.perform(post("/clientes/{id}/transacoes", 1L)
                        .contentType("application/json")
                        .content("{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"toma\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limite", is(100000)))
                .andExpect(jsonPath("$.saldo", is(1)))
        ;

    }
}
