package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BeneficioControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired BeneficioRepository repo;

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
    void shouldListBenefits() throws Exception {
        mvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetBenefitById() throws Exception {
        mvc.perform(get("/api/v1/beneficios/{id}", b1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(b1.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("A")));
    }

    @Test
    void shouldCreateBenefit() throws Exception {
        var payload = new BeneficioEntity();
        payload.setNome("C");
        payload.setDescricao("desc C");
        payload.setValor(new BigDecimal("10.00"));
        payload.setAtivo(true);

        mvc.perform(post("/api/v1/beneficios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("C")));
    }

    @Test
    void shouldUpdateBenefit() throws Exception {
        var payload = new BeneficioEntity();
        payload.setNome("A2");
        payload.setDescricao("desc A2");
        payload.setValor(new BigDecimal("101.00"));
        payload.setAtivo(false);

        mvc.perform(put("/api/v1/beneficios/{id}", b1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("A2")))
                .andExpect(jsonPath("$.valor", is(101.00)));
    }

    @Test
    void shouldDeleteBenefit() throws Exception {
        mvc.perform(delete("/api/v1/beneficios/{id}", b1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldTransferAndReturnNoContent() throws Exception {
        var req = new BeneficioController.TransferRequest(b1.getId(), b2.getId(), new BigDecimal("30.00"));

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        var from = repo.findById(b1.getId()).orElseThrow();
        var to = repo.findById(b2.getId()).orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("70.00"), from.getValor());
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("80.00"), to.getValor());
    }
}