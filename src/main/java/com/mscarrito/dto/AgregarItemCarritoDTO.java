package com.mscarrito.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AgregarItemCarritoDTO {
    private UUID combinacionId;
    private int cantidad;
}
