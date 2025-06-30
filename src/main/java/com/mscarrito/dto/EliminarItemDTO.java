package com.mscarrito.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class EliminarItemDTO {
    private UUID combinacionId;
    private UUID usuarioId;
}
