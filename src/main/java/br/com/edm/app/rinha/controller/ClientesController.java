package br.com.edm.app.rinha.controller;

import br.com.edm.app.rinha.model.*;
import br.com.edm.app.rinha.service.ClientesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClientesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientesController.class);
    private final ClientesService service;

    public ClientesController(ClientesService service) {
        this.service = service;
    }

    @PostMapping(value = "/{id}/transacoes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TransacaoClienteResponse> transacionar(@PathVariable("id") Long id, @Valid @RequestBody Transacoes.TransacaoClienteRequest transacao) {
        final Transacoes transforma = Transacoes.transforma(transacao);
        transforma.validarTransacao();
        return ResponseEntity.ok(service.handleTransacao(id, transforma));
    }



    @GetMapping(value = "/{id}/extrato", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExtratoResponse> extratos(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.handleExtratoResponse(id));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
