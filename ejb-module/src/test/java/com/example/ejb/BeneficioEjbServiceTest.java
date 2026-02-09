package com.example.ejb;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeneficioEjbServiceTest {  @Mock
EntityManager em;

    BeneficioEjbService service;

    @BeforeEach
    void setup() throws Exception {
        service = new BeneficioEjbService();

        // injeta mock no campo privado "em"
        Field f = BeneficioEjbService.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(service, em);
    }

    @Test
    void shouldThrowWhenIdsAreNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(null, 2L, BigDecimal.ONE));

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, null, BigDecimal.ONE));
    }

    @Test
    void shouldThrowWhenIdsAreEqual() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 1L, BigDecimal.ONE));
    }

    @Test
    void shouldThrowWhenAmountIsNullOrZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, null));

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ZERO));

        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("-1")));
    }

    @Test
    void shouldThrowWhenBenefitNotFound() {
        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(null);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(new Beneficio(2L, "B"));

        assertThrows(EntityNotFoundException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE));

        verify(em, never()).flush();
    }

    @Test
    void shouldLockInOrderToAvoidDeadlock_lowestIdFirst() {
        Beneficio b1 = beneficio(1L, "A", "10");
        Beneficio b2 = beneficio(2L, "B", "10");

        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(b1);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(b2);

        service.transfer(2L, 1L, new BigDecimal("1.00")); // invertido propositalmente

        InOrder inOrder = inOrder(em);
        inOrder.verify(em).find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE);
        inOrder.verify(em).find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void shouldThrowWhenInsufficientBalance_andNotChangeValues() {
        Beneficio from = beneficio(1L, "FROM", "5.00");
        Beneficio to   = beneficio(2L, "TO",   "1.00");

        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(from);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(to);

        assertThrows(IllegalStateException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("10.00")));

        assertEquals(new BigDecimal("5.00"), from.getValor());
        assertEquals(new BigDecimal("1.00"), to.getValor());
        verify(em, never()).flush();
    }

    @Test
    void shouldTransferSuccessfully_debitAndCredit_andFlush() {
        Beneficio from = beneficio(1L, "FROM", "10.00");
        Beneficio to   = beneficio(2L, "TO",   "3.00");

        when(em.find(eq(Beneficio.class), eq(1L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(from);
        when(em.find(eq(Beneficio.class), eq(2L), eq(LockModeType.PESSIMISTIC_WRITE))).thenReturn(to);

        service.transfer(1L, 2L, new BigDecimal("4.00"));

        assertEquals(new BigDecimal("6.00"), from.getValor());
        assertEquals(new BigDecimal("7.00"), to.getValor());
        verify(em).flush();
    }

    private Beneficio beneficio(Long id, String nome, String valor) {
        Beneficio b = new Beneficio(id, nome);
        b.setValor(new BigDecimal(valor));
        return b;
    }
}