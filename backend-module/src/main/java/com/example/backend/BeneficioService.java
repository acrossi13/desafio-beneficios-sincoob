package com.example.backend;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BeneficioService {

    private final BeneficioRepository repo;


    public BeneficioService(BeneficioRepository repo) {
        this.repo = repo;
    }

    public List<BeneficioEntity> findAll() {
        return repo.findAll();
    }

    public BeneficioEntity findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    public BeneficioEntity create(BeneficioEntity e) {
        return repo.save(e);
    }

    @Transactional
    public BeneficioEntity update(Long id, BeneficioEntity e) {
        BeneficioEntity current = findById(id);
        current.setNome(e.getNome());
        current.setValor(e.getValor());
        return repo.save(current);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal valor) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("ids obrigatórios");
        if (fromId.equals(toId)) throw new IllegalArgumentException("fromId e toId iguais");
        if (valor == null || valor.signum() <= 0) throw new IllegalArgumentException("valor inválido");

        Long firstId = fromId < toId ? fromId : toId;
        Long secondId = fromId < toId ? toId : fromId;

        BeneficioEntity first = repo.findByIdForUpdate(firstId)
                .orElseThrow(() -> new EntityNotFoundException("benefício não encontrado: " + firstId));
        BeneficioEntity second = repo.findByIdForUpdate(secondId)
                .orElseThrow(() -> new EntityNotFoundException("benefício não encontrado: " + secondId));

        BeneficioEntity from = fromId.equals(firstId) ? first : second;
        BeneficioEntity to = toId.equals(firstId) ? first : second;

        BigDecimal saldo = from.getValor() == null ? BigDecimal.ZERO : from.getValor();
        if (saldo.compareTo(valor) < 0) throw new IllegalStateException("saldo insuficiente");

        from.setValor(saldo.subtract(valor));
        BigDecimal toSaldo = to.getValor() == null ? BigDecimal.ZERO : to.getValor();
        to.setValor(toSaldo.add(valor));
    }
}
