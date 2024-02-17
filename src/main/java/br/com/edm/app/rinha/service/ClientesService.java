package br.com.edm.app.rinha.service;

import br.com.edm.app.rinha.model.*;
import br.com.edm.app.rinha.repositories.ClientesRepository;
import br.com.edm.app.rinha.repositories.TransacoesRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;
    private final TransacoesRepository transacoesRepository;
    public ClientesService(ClientesRepository clientesRepository, TransacoesRepository transacoesRepository) {
        this.clientesRepository = clientesRepository;
        this.transacoesRepository = transacoesRepository;
    }
    @Transactional
    public TransacaoClienteResponse handleTransacao(Long id, Transacoes transacao) {

        int ret = clientesRepository.updateSaldoCliente(id, "d".equals(transacao.getTipo()) ? -transacao.getValor() : transacao.getValor());
        if (ret == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Clientes cli = clientesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (cli.getSaldo() < -cli.getLimite()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        transacao.setIdClientes(id);
        transacao.setRealizadaEm(OffsetDateTime.now());
        transacoesRepository.addTransacao(transacao);

        return new TransacaoClienteResponse(cli.getLimite(), cli.getSaldo());
    }

    public ExtratoResponse handleExtratoResponse(Long id) {
        final Clientes cli = clientesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        final List<TransacaoExtrato> top10Transacoes = transacoesRepository.getTop10Transacoes(id);
        return new ExtratoResponse(
                    new SaldoResponse(cli.getSaldo(), LocalDateTime.now(), cli.getLimite()),
                    top10Transacoes
                );
    }
}
