package com.example.backend.beneficio.api.controller;

import com.example.backend.beneficio.api.dto.BeneficioRequest;
import com.example.backend.beneficio.api.dto.BeneficioResponse;
import com.example.backend.beneficio.api.dto.TransferRequest;
import com.example.backend.beneficio.api.domain.BeneficioEntity;
import com.example.backend.beneficio.api.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Benefícios", description = "CRUD de benefícios e transferência de valores")
@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar benefícios")
    @GetMapping
    public List<BeneficioResponse> list() {
        return service.findAll();
    }

    @Operation(summary = "Buscar benefício por ID")
    @GetMapping("/{id}")
    public BeneficioResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Criar benefício")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioResponse create(@Valid @RequestBody BeneficioRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Atualizar benefício")
    @PutMapping("/{id}")
    public BeneficioResponse update(@PathVariable Long id, @Valid @RequestBody BeneficioRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Deletar benefício")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(
            summary = "Transferir valor entre benefícios",
            description = "Valida valor > 0, benefícios existentes e saldo suficiente. Retorna 204 quando ok."
    )
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest req) {
        service.transfer(req.fromId(), req.toId(), req.valor());
    }
}
