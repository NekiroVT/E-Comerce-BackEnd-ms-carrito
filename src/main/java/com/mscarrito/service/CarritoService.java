package com.mscarrito.service;

import com.mscarrito.dto.ListarCompletoCarrito;
import com.mscarrito.dto.ListarPocoCarrito;
import com.mscarrito.dto.MostrarSimpleCarrito;
import com.mscarrito.entities.Carrito;

import java.util.List;
import java.util.UUID;

public interface CarritoService {
    void agregarAlCarrito(UUID usuarioId, UUID combinacionId, int cantidad);
    Carrito obtenerOCrearCarrito(UUID usuarioId);
    void actualizarCantidad(UUID usuarioId, UUID combinacionId, int nuevaCantidad);
    public List<MostrarSimpleCarrito> obtenerItemsSimples(UUID usuarioId);

    List<ListarPocoCarrito> listarItemsPoco(UUID usuarioId);
    List<ListarCompletoCarrito> listarItemsCompletos(UUID usuarioId);
    void eliminarItemDelCarrito(UUID combinacionId, UUID usuarioId);




}
