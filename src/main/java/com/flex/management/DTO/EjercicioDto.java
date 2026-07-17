package com.flex.management.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EjercicioDto {
    private String nombre;
    private Integer series;
    private String repeticiones; // String por si quieren poner "Fallo", "10-12", etc.
}