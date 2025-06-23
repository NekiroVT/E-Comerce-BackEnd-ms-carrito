package com.mscarrito.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MostrarSimpleCarrito {
    private UUID combinacionId;
    private int cantidad;
}
