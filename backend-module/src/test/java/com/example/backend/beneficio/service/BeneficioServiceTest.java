package com.example.backend.beneficio.service;

import com.example.backend.domain.BeneficioEntity;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.service.BeneficioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeneficioServiceTest {

    @Autowired
    BeneficioService service;
    @Autowired
    BeneficioRepository repo;

    private BeneficioEntity b1;
    private BeneficioEntity b2;

    @BeforeEach
    void setup() {
        repo.deleteAll();

        b1 = new BeneficioEntity();
        b1.setNome("A");
        b1.setDescricao("desc A");
        b1.setValor(new BigDecimal("100.00"));
        b1.setAtivo(true);
        b1 = repo.save(b1);

        b2 = new BeneficioEntity();
        b2.setNome("B");
        b2.setDescricao("desc B");
        b2.setValor(new BigDecimal("50.00"));
        b2.setAtivo(true);
        b2 = repo.save(b2);
    }

    @Test
    void shouldTransferAmountWhenBalanceIsSufficient() {
        service.transfer(b1.getId(), b2.getId(), new BigDecimal("30.00"));

        var from = repo.findById(b1.getId()).orElseThrow();
        var to = repo.findById(b2.getId()).orElseThrow();

        assertEquals(new BigDecimal("70.00"), from.getValor());
        assertEquals(new BigDecimal("80.00"), to.getValor());
    }

    @Test
    void shouldFailWhenAmountIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(b1.getId(), b2.getId(), null));
    }

    @Test
    void shouldFailWhenAmountIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(b1.getId(), b2.getId(), BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(b1.getId(), b2.getId(), new BigDecimal("-1")));
    }

    @Test
    void shouldFailWhenFromAndToAreTheSame() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(b1.getId(), b1.getId(), new BigDecimal("1.00")));
    }

    @Test
    void shouldFailWhenBalanceIsInsufficient() {
        assertThrows(IllegalStateException.class,
                () -> service.transfer(b2.getId(), b1.getId(), new BigDecimal("999.00")));
    }

    @Test
    void shouldFailWhenAnyAccountDoesNotExist() {
        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> service.transfer(999L, b2.getId(), new BigDecimal("1.00")));
        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> service.transfer(b1.getId(), 999L, new BigDecimal("1.00")));
    }

    @Test
    void shouldCreateUpdateAndDeleteBenefit() {
        var created = new BeneficioEntity();
        created.setNome("C");
        created.setDescricao("desc C");
        created.setValor(new BigDecimal("10.00"));
        created.setAtivo(true);

        var saved = service.create(created);
        assertNotNull(saved.getId());

        var patch = new BeneficioEntity();
        patch.setNome("C2");
        patch.setDescricao("desc C2");
        patch.setValor(new BigDecimal("11.00"));
        patch.setAtivo(false);

        var updated = service.update(saved.getId(), patch);
        assertEquals("C2", updated.getNome());
        assertEquals(new BigDecimal("11.00"), updated.getValor());

        service.delete(saved.getId());
        assertTrue(repo.findById(saved.getId()).isEmpty());
    }
}