package com.flex.management.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flex.management.Enum.Estados;
import com.flex.management.Enum.MetodosPagos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table(name = "Pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pago {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "socio_dni") // Renombrado para que coincida con el DNI
    private Socio socio;

    @Column
    private LocalDateTime fechaPago; // Cambiado a LocalDateTime para tener la hora exacta

    @Column
    private Double monto; // Cambiado a Double para evitar problemas con Mercado Pago

    @Enumerated(EnumType.STRING)
    private MetodosPagos metodoPago;

    @Enumerated(EnumType.STRING)
    private Estados estado;
    
    @Column(unique = true)
    private Long idMercadoPago;
}
