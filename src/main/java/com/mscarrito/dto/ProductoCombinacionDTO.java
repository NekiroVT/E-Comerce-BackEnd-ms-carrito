package com.mscarrito.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductoCombinacionDTO {
    private UUID id;               // ID de la combinación
    private BigDecimal precio;     // Precio actual
    private Integer stock;         // Stock disponible
    private List<String> imagenes;
    private String nombre;
    private String valorClave1;  // Valor de la primera clave
    private String valorClave2;
    private UUID productoId;

}

