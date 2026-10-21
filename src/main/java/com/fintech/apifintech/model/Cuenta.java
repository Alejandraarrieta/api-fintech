package com.fintech.apifintech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cuentas")
public class Cuenta extends EntidadAuditada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 22)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoCuenta tipo;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    protected Cuenta() { }

    public Cuenta(String numero, TipoCuenta tipo, BigDecimal saldo, Usuario usuario) {
        this.numero = numero;
        this.tipo = tipo;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    public void actualizar(String numero, TipoCuenta tipo, BigDecimal saldo, Usuario usuario) {
        this.numero = numero;
        this.tipo = tipo;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public TipoCuenta getTipo() { return tipo; }
    public BigDecimal getSaldo() { return saldo; }
    public Usuario getUsuario() { return usuario; }
}
