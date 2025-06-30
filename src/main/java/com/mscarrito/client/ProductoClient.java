package com.mscarrito.client;

import com.mscarrito.dto.ProductoCombinacionDTO;

import com.mscarrito.dto.UsuarioCarritoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "ms-productos") // Sin path aquí
public interface ProductoClient {

    @GetMapping("/api/combinaciones/{id}") // Ruta completa aquí
    ProductoCombinacionDTO obtenerCombinacionPorId(@PathVariable("id") UUID id);
    @GetMapping("/api/combinaciones/{id}/producto-id")
    UUID obtenerProductoIdPorCombinacion(@PathVariable("id") UUID id);
    @GetMapping("/api/productos/usuario/{usuarioId}")
    UsuarioCarritoDTO obtenerUsuarioParaCarrito(@PathVariable("usuarioId") UUID usuarioId);




}
