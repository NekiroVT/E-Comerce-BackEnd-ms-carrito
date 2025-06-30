package com.mscarrito.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ListarCompletoCarrito {
    private UUID combinacionId;
    private int cantidad;
    private Double precioUnitario;
    private BigDecimal total;
    private String imagenUrl;
    private String valorClave1; // Valor de la primera clave
    private String valorClave2; // Valor de la segunda clave
    private String nombreProducto; // Nombre del producto
    private String firstName; // Nombre del usuario
    private String lastName; // Apellido del usuario
}
