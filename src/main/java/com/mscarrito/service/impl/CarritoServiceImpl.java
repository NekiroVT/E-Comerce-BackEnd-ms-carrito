package com.mscarrito.service.impl;

import com.mscarrito.client.ProductoClient;
import com.mscarrito.dto.ListarCompletoCarrito;
import com.mscarrito.dto.ListarPocoCarrito;
import com.mscarrito.dto.ProductoCombinacionDTO;
import com.mscarrito.dto.MostrarSimpleCarrito;
import com.mscarrito.entities.Carrito;
import com.mscarrito.entities.CarritoItem;
import com.mscarrito.repository.CarritoItemRepository;
import com.mscarrito.repository.CarritoRepository;
import com.mscarrito.service.CarritoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository itemRepository;
    private final ProductoClient productoClient;

    @Override
    @Transactional
    public void agregarAlCarrito(UUID usuarioId, UUID combinacionId, int cantidad) {
        // ✅ Validar cantidad
        if (cantidad <= 0) {
            throw new RuntimeException("❌ La cantidad debe ser mayor a 0");
        }

        // ✅ Obtener la combinación desde MS-Productos
        ProductoCombinacionDTO dto = productoClient.obtenerCombinacionPorId(combinacionId);
        if (dto == null) {
            throw new RuntimeException("❌ La combinación no existe");
        }

        if (dto.getStock() < cantidad) {
            throw new RuntimeException("❌ Stock insuficiente. Disponible: " + dto.getStock());
        }

        // ✅ Obtener o crear carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setId(UUID.randomUUID());
                    nuevo.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevo);
                });

        // ✅ Buscar si ya hay un ítem con esa combinación
        Optional<CarritoItem> existenteOpt = itemRepository.findByCarritoAndCombinacionId(carrito, combinacionId);

        if (existenteOpt.isPresent()) {
            CarritoItem item = existenteOpt.get();
            int nuevaCantidad = item.getCantidad() + cantidad;

            if (nuevaCantidad > dto.getStock()) {
                throw new RuntimeException("❌ Solo puedes tener hasta " + dto.getStock() + " unidades en total");
            }

            item.setCantidad(nuevaCantidad);
            item.setPrecioUnitario(dto.getPrecio());
            item.setTotal(dto.getPrecio().multiply(BigDecimal.valueOf(nuevaCantidad)));
            itemRepository.save(item);

        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setId(UUID.randomUUID());
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCombinacionId(combinacionId);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(dto.getPrecio());
            nuevoItem.setTotal(dto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
            itemRepository.save(nuevoItem);
        }
    }

    @Override
    @Transactional
    public Carrito obtenerOCrearCarrito(UUID usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    nuevo.setId(UUID.randomUUID());
                    return carritoRepository.save(nuevo);
                });
    }

    @Override
    public void actualizarCantidad(UUID usuarioId, UUID combinacionId, int nuevaCantidad) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("🛒 Carrito no encontrado para el usuario"));

        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getCombinacionId().equals(combinacionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ Combinación no encontrada en el carrito"));

        if (nuevaCantidad <= 0) {
            carrito.getItems().remove(item);
        } else {
            item.setCantidad(nuevaCantidad);
            item.setTotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
        }

        carritoRepository.save(carrito);
    }

    @Override
    public List<MostrarSimpleCarrito> obtenerItemsSimples(UUID usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return carrito.getItems().stream().map(item -> {
            MostrarSimpleCarrito dto = new MostrarSimpleCarrito();
            dto.setCombinacionId(item.getCombinacionId());
            dto.setCantidad(item.getCantidad());
            return dto;
        }).toList();
    }

    @Override
    public List<ListarPocoCarrito> listarItemsPoco(UUID usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        return carrito.getItems().stream().map(item -> {
            ProductoCombinacionDTO combinacion = productoClient.obtenerCombinacionPorId(item.getCombinacionId());

            ListarPocoCarrito dto = new ListarPocoCarrito();
            dto.setCombinacionId(item.getCombinacionId());
            dto.setCantidad(item.getCantidad());
            dto.setTotal(item.getTotal());

            if (combinacion.getImagenes() != null && !combinacion.getImagenes().isEmpty()) {
                dto.setImagenUrl(combinacion.getImagenes().get(0)); // Primera imagen
            } else {
                dto.setImagenUrl(null); // O una imagen por defecto si querés
            }

            return dto;
        }).toList();
    }

    @Override
    public List<ListarCompletoCarrito> listarItemsCompletos(UUID usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        return carrito.getItems().stream().map(item -> {
            ProductoCombinacionDTO combinacion = productoClient.obtenerCombinacionPorId(item.getCombinacionId());

            ListarCompletoCarrito dto = new ListarCompletoCarrito();
            dto.setCombinacionId(item.getCombinacionId());
            dto.setCantidad(item.getCantidad());
            dto.setTotal(item.getTotal());

            if (combinacion.getImagenes() != null && !combinacion.getImagenes().isEmpty()) {
                dto.setImagenUrl(combinacion.getImagenes().get(0)); // Primera imagen
            } else {
                dto.setImagenUrl(null); // O una imagen por defecto si deseas
            }

            return dto;
        }).toList();
    }











}
