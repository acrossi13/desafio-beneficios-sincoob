package com.example.ejb;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "beneficio")
public class Beneficio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 19, scale = 2)
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
