package com.flex.management.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Configuracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String clave;
    
    private Double valor;

    // Constructores, Getters y Setters
    public Configuracion() {}

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
}