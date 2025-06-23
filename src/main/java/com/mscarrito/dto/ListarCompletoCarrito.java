package com.mscarrito.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ListarCompletoCarrito {
    private UUID combinacionId;
    private int cantidad;
    private BigDecimal total;
    private String imagenUrl;

}
