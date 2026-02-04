package com.example.ejb;

import java.io.Serializable;
import java.math.BigDecimal;

public class Beneficio implements Serializable {

    private Long id;
    private String nome;
    private BigDecimal valor;

    public Beneficio() {
    }

    public Beneficio(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id; }

    public String getNome() {
        return this.nome; }

    public void setNome(String nome) {
        this.nome = nome; }

    public BigDecimal getValor() {
        return valor; }

    public void setValor(BigDecimal valor) {
        this.valor = valor; }
}
