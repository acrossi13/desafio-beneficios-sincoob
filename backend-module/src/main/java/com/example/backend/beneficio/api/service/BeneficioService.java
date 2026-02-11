package com.example.backend.beneficio.api.service;

import com.example.backend.beneficio.api.domain.BeneficioEntity;
import com.example.backend.beneficio.api.dto.BeneficioMapper;
import com.example.backend.beneficio.api.dto.BeneficioRequest;
import com.example.backend.beneficio.api.dto.BeneficioResponse;
import com.example.backend.beneficio.api.repository.BeneficioRepository;
import com.example.ejb.Beneficio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BeneficioService {

    private final BeneficioRepository repo;

    private static BeneficioResponse toResponse(BeneficioEntity e) {
        return new BeneficioResponse(e.getId(), e.getNome(), e.getDescricao(), e.getValor(), e.getAtivo(), e.getVersion());
    }


    public BeneficioService(BeneficioRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public List<BeneficioResponse> findAll() {
        return repo.findAll().stream().map(BeneficioMapper::toResponse).toList();
    }

    @Transactional
    public BeneficioResponse findById(Long id) {
        var e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Benefício não encontrado: " + id));
        return toResponse(e);
    }

    @Transactional
    public BeneficioResponse create(BeneficioRequest req) {
        var e = new BeneficioEntity();
        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setValor(req.valor());
        e.setAtivo(req.ativo() == null || req.ativo());
        var saved = repo.save(e);
        return toResponse(saved);
    }

    @Transactional
    public BeneficioResponse update(Long id, BeneficioRequest req) {
        var e = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Benefício não encontrado: " + id));

        e.setNome(req.nome());
        e.setDescricao(req.descricao());
        e.setValor(req.valor());
        if (req.ativo() != null) e.setAtivo(req.ativo());

        return toResponse(repo.save(e));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Benefício não encontrado: " + id);
        }
        repo.deleteById(id);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal valor) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("Ids obrigatórios.");
        if (fromId.equals(toId)) throw new IllegalArgumentException("From (Id) e to (Id) iguais.");
        if (valor == null || valor.signum() <= 0) throw new IllegalArgumentException("Valor inválido.");

        Long firstId = fromId < toId ? fromId : toId;
        Long secondId = fromId < toId ? toId : fromId;

        BeneficioEntity first = repo.findByIdForUpdate(firstId)
                .orElseThrow(() -> new EntityNotFoundException("Benefício não encontrado: " + firstId));
        BeneficioEntity second = repo.findByIdForUpdate(secondId)
                .orElseThrow(() -> new EntityNotFoundException("Benefício não encontrado: " + secondId));

        BeneficioEntity from = fromId.equals(firstId) ? first : second;
        BeneficioEntity to = toId.equals(firstId) ? first : second;

        BigDecimal saldo = from.getValor() == null ? BigDecimal.ZERO : from.getValor();
        if (saldo.compareTo(valor) < 0) throw new IllegalStateException("Saldo insuficiente.");

        from.setValor(saldo.subtract(valor));
        BigDecimal toSaldo = to.getValor() == null ? BigDecimal.ZERO : to.getValor();
        to.setValor(toSaldo.add(valor));
    }
}
