package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(rollbackOn = Exception.class)
    public void transfer(Long fromId, Long toId, BigDecimal valor) {
        if (fromId == null || toId == null) throw new IllegalArgumentException("ids obrigatórios");
        if (Objects.equals(fromId, toId)) throw new IllegalArgumentException("fromId e toId iguais");
        if (valor == null || valor.signum() <= 0) throw new IllegalArgumentException("valor inválido");

        // ordem pra evitar deadlock
        Long firstId = fromId < toId ? fromId : toId;
        Long secondId = fromId < toId ? toId : fromId;

        Beneficio first = em.find(Beneficio.class, firstId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio second = em.find(Beneficio.class, secondId, LockModeType.PESSIMISTIC_WRITE);

        if (first == null || second == null) throw new EntityNotFoundException("benefício não encontrado");

        Beneficio from = fromId.equals(firstId) ? first : second;
        Beneficio to = toId.equals(firstId) ? first : second;

        BigDecimal saldo = from.getValor() == null ? BigDecimal.ZERO : from.getValor();
        if (saldo.compareTo(valor) < 0) throw new IllegalStateException("saldo insuficiente");


        from.setValor(from.getValor().subtract(valor));
        to.setValor(to.getValor().add(valor));

        em.flush(); // força detectar problemas ainda dentro da tx
    }

//    public void transfer(Long fromId, Long toId, BigDecimal amount) {
//        Beneficio from = em.find(Beneficio.class, fromId);
//        Beneficio to   = em.find(Beneficio.class, toId);
//
//        // BUG: sem validações, sem locking, pode gerar saldo negativo e lost update
//        from.setValor(from.getValor().subtract(amount));
//        to.setValor(to.getValor().add(amount));
//
//        em.merge(from);
//        em.merge(to);
//    }
}
