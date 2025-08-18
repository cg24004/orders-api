package com.devsoft.orders_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// Esta clase es un DTO (Data Transfer Object) que representa una categoría
// y permite transferir datos entra el controlador y el servicio
public class CategoriaDTO {
    private Long id;
    private String nombre;
}
