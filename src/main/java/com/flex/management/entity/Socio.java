package com.flex.management.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Socio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Socio {
  @Id
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String telefono;

    @Column(nullable = false)
    private LocalDate fechaVencimientoCuota;

    @Column(nullable = false)
    private boolean activo;

    // Aislamos la lista para evitar el StackOverflowError
    @ToString.Exclude 
    @OneToMany(mappedBy = "socio")
    private List<Pago> pagos;
}
