package com.flex.management.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RutinaDto {
    private String nombreRutina;
    private List<EjercicioDto> ejercicios;
}