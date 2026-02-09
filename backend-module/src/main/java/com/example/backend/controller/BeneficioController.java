package com.example.backend.controller;

import com.example.backend.domain.BeneficioEntity;
import com.example.backend.service.BeneficioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/beneficios")
public class BeneficioController {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @GetMapping
    public List<BeneficioEntity> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BeneficioEntity get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficioEntity create(@RequestBody BeneficioEntity e) {
        return service.create(e);
    }

    @PutMapping("/{id}")
    public BeneficioEntity update(@PathVariable Long id, @RequestBody BeneficioEntity e) {
        return service.update(id, e);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    public record TransferRequest(Long fromId, Long toId, BigDecimal valor) {}

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest req) {
        service.transfer(req.fromId(), req.toId(), req.valor());
    }
}
