package com.mscarrito.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductoUsuarioRequestDTO {
    private UUID productoId;
    private UUID usuarioId;
}
