package br.com.edm.app.rinha.controller;

import br.com.edm.app.rinha.model.ExtratoResponse;
import br.com.edm.app.rinha.model.TransacaoClienteRequest;
import br.com.edm.app.rinha.model.TransacaoClienteResponse;
import br.com.edm.app.rinha.model.Transacoes;
import br.com.edm.app.rinha.service.ClientesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/clientes")
public class ClientesController {

    private final ObjectMapper objectMapper;
    private final ClientesService service;

    public ClientesController(ObjectMapper objectMapper, ClientesService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @PostMapping(value = "/{id}/transacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoClienteResponse> transacionar(@PathVariable("id") Long id, @RequestBody String request) {
        final TransacaoClienteRequest transacaoClienteRequest = validarTransacaoRequest(request);
        if (transacaoClienteRequest != null)
            return ResponseEntity.ok(service.handleTransacao(id, Transacoes.transforma(transacaoClienteRequest)));
        else
            return ResponseEntity.unprocessableEntity().build();
    }

    private TransacaoClienteRequest validarTransacaoRequest(String request) {
        TransacaoClienteRequest req;
        try {
            req = objectMapper.readValue(request, TransacaoClienteRequest.class);
            if (! verificar(req))
                return null;
            return req;
        } catch (JsonProcessingException | NumberFormatException e) {
            return null;
        }
    }

    private boolean verificar(TransacaoClienteRequest req) {
        return req.valor() != null &&
                Integer.parseInt(req.valor()) > 0 &&
                req.descricao() != null &&
                !req.descricao().isEmpty() &&
                req.descricao().length() <= 10 &&
                Arrays.asList("c", "d").contains(req.tipo());
    }


    @GetMapping(value = "/{id}/extrato", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExtratoResponse> extratos(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.handleExtratoResponse(id));
    }

}
