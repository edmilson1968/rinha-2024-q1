package br.com.edm.app.rinha;

import br.com.edm.app.rinha.model.*;
import br.com.edm.app.rinha.repositories.ClientesRepository;
import br.com.edm.app.rinha.repositories.TransacoesRepository;
import br.com.edm.app.rinha.service.ClientesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

//@SpringBootTest
class Rinha2024Q1ApplicationTests {

	ClientesRepository clientesRepository;
	TransacoesRepository transacoesRepository;
	ClientesService clientesService;

	@BeforeEach
	void setup() {
		clientesRepository = mock(ClientesRepository.class);
		transacoesRepository = mock(TransacoesRepository.class);
		clientesService = new ClientesService(clientesRepository, transacoesRepository);
	}

//	@ParameterizedTest
//	@ValueSource(strings = {
//			"{\"valor\": 1.2, \"tipo\": \"d\", \"descricao\": \"devolve\"}",
//			"{\"valor\": 1, \"tipo\": \"x\", \"descricao\": \"devolve\"}",
//			"{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"123456789 e mais um pouco\"}",
//			"{\"valor\": 1, \"tipo\": \"c\", \"descricao\": \"\"}",
//			"{\"valor\": 1, \"tipo\": \"c\", \"descricao\": null}"
//	})
//	void shouldValidateTransacoes(String body) throws JsonProcessingException {
//		final ObjectMapper objectMapper = new ObjectMapper();
//		final Transacoes.TransacaoClienteRequest transacaoClienteRequest = objectMapper.readValue(body, Transacoes.TransacaoClienteRequest.class);
//		final Transacoes transacao = Transacoes.validarTransacao(transacaoClienteRequest);
//		assertThatThrownBy(() -> transacao.validarTransacao())
//				.isInstanceOf(ResponseStatusException.class)
//				.extracting("status")
//				.isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
//	}

	@Test
	void shouldReturnBalance() {

		when(clientesRepository.findById(1L)).thenReturn(Optional.of(new Clientes(100000, 1)));
		when(transacoesRepository.getTop10Transacoes(1L)).thenReturn(
				List.of(
						new TransacaoExtrato(10, "c", "descricao", LocalDateTime.now()),
						new TransacaoExtrato(90000, "d", "descricao", LocalDateTime.now())
				)
		);

		final ExtratoResponse extratoResponse = clientesService.handleExtratoResponse(1L);
		assertThat(extratoResponse).isNotNull();

		final SaldoResponse saldoResponse = extratoResponse.saldo();
		assertThat(saldoResponse).isInstanceOf(SaldoResponse.class);
		assertThat(saldoResponse.total()).isEqualTo(1);
		assertThat(saldoResponse.limite()).isEqualTo(100000);

		final List<TransacaoExtrato> ultimasTransacoes = extratoResponse.ultimasTransacoes();
		assertThat(ultimasTransacoes).isInstanceOf(List.class);
		assertThat(ultimasTransacoes).hasOnlyElementsOfType(TransacaoExtrato.class);

		verify(clientesRepository).findById(1L);
		verify(transacoesRepository).getTop10Transacoes(1L);
	}

	@Test
	void shouldThrowExceptionOnClienteNotFoundOnBalance() {
		when(clientesRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(
				() -> clientesService.handleExtratoResponse(1L)
		).isInstanceOf(ResponseStatusException.class)
				.extracting("status")
				.isEqualTo(HttpStatus.NOT_FOUND);

		verify(clientesRepository).findById(1L);
		verify(transacoesRepository, times(0)).getTop10Transacoes(1L);
	}

	@Test
	void shouldThrowNotFoundOnAddTransaction() {
		final Transacoes transacao = new Transacoes(1L, 100000, "d", "descricao", null);
		when(clientesRepository.atualizaSaldoCliente(1L, 100000)).thenReturn(Optional.empty());
		assertThatThrownBy(
				() -> clientesService.handleTransacao(1L, transacao)
		).isInstanceOf(ResponseStatusException.class)
				.extracting("status")
				.isEqualTo(HttpStatus.NOT_FOUND);
		verify(transacoesRepository, times(0)).addTransacao(transacao);
	}

	@Test
	void shouldThrowUnprocessableEntityOnAddTransaction() {
		final Transacoes transacao = new Transacoes(1L, 100000, "d", "descricao", null);
		when(clientesRepository.atualizaSaldoCliente(1L, -100000)).thenReturn(Optional.of(new Clientes(10000, -100001)));
		when(clientesRepository.findById(1L)).thenReturn(Optional.of(new Clientes(100000, -100001)));
		assertThatThrownBy(
				() -> clientesService.handleTransacao(1L, transacao)
		).isInstanceOf(ResponseStatusException.class)
				.extracting("status")
				.isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		verify(transacoesRepository, times(0)).addTransacao(transacao);
	}

	@Test
	void shouldAddTransacao() {
		final Transacoes transacao = Mockito.spy(new Transacoes(1L, 1, "c", "descricao", null));
		when(clientesRepository.atualizaSaldoCliente(1L, 1)).thenReturn(Optional.of(new Clientes(100000, 0)));
		final Clientes cli = new Clientes(100000, 0);
		when(clientesRepository.findById(1L)).thenReturn(Optional.of(cli));
		doNothing().when(transacoesRepository).addTransacao(transacao);
		final TransacaoClienteResponse transacaoClienteResponse = clientesService.handleTransacao(1L, transacao);

		assertThat(transacaoClienteResponse).isNotNull();
		assertThat(transacaoClienteResponse.limite()).isEqualTo(cli.getLimite());
		assertThat(transacaoClienteResponse.saldo()).isEqualTo(cli.getSaldo());

		verify(transacao).setIdClientes(1L);
		verify(transacao).setRealizadaEm(any(OffsetDateTime.class));
	}
}
