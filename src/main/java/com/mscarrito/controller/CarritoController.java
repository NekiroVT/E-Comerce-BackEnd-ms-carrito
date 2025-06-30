package com.mscarrito.controller;

import com.mscarrito.dto.*;
import com.mscarrito.entities.Carrito;
import com.mscarrito.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;



    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(
            @RequestBody AgregarItemCarritoDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader
    ) {
        UUID usuarioId = UUID.fromString(userIdHeader);
        carritoService.agregarAlCarrito(usuarioId, dto.getCombinacionId(), dto.getCantidad());

        Map<String, Object> respuesta = Map.of(
                "success", true,
                "message", "✅ Producto agregado al carrito"
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/obtener")
    public ResponseEntity<?> obtenerCarrito(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID usuarioId = UUID.fromString(userIdHeader);
        List<MostrarSimpleCarrito> items = carritoService.obtenerItemsSimples(usuarioId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("items", items);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/actualizar-cantidad")
    public ResponseEntity<Map<String, Object>> actualizarCantidad(
            @RequestBody ActualizarCantidadDTO dto,
            @RequestHeader("X-User-Id") String userIdHeader
    ) {
        UUID usuarioId = UUID.fromString(userIdHeader);
        carritoService.actualizarCantidad(usuarioId, dto.getCombinacionId(), dto.getCantidad());
        return ResponseEntity.ok(Map.of("success", true, "message", "Cantidad actualizada"));
    }

    @GetMapping("/listar-poco")
    public ResponseEntity<?> listarPoco(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID usuarioId = UUID.fromString(userIdHeader);
        List<ListarPocoCarrito> items = carritoService.listarItemsPoco(usuarioId);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "items", items
                )
        );
    }

    @GetMapping("/listar-completo")
    public ResponseEntity<?> listarCompleto(@RequestHeader("X-User-Id") String userIdHeader) {
        UUID usuarioId = UUID.fromString(userIdHeader);
        List<ListarCompletoCarrito> items = carritoService.listarItemsCompletos(usuarioId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "items", items
        ));
    }

    @DeleteMapping("/eliminar/{combinacionId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable UUID combinacionId,
            @RequestHeader("X-User-Id") UUID usuarioId
    ) {
        carritoService.eliminarItemDelCarrito(combinacionId, usuarioId);
        return ResponseEntity.ok().build();
    }















}
