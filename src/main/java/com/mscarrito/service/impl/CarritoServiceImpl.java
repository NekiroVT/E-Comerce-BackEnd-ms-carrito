package com.mscarrito.service.impl;

import com.mscarrito.client.ProductoClient;
import com.mscarrito.dto.*;
import com.mscarrito.entities.Carrito;
import com.mscarrito.entities.CarritoItem;
import com.mscarrito.repository.CarritoItemRepository;
import com.mscarrito.repository.CarritoRepository;
import com.mscarrito.service.CarritoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CarritoItemRepository carritoItemRepository;


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

        // ✅ Obtener los datos del usuario (firstName, lastName) desde MS-Productos
        UsuarioCarritoDTO usuarioCarritoDTO = productoClient.obtenerUsuarioParaCarrito(usuarioId);
        String firstName = usuarioCarritoDTO.getFirstName();
        String lastName = usuarioCarritoDTO.getLastName();

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

            // Actualizar el ítem con la nueva cantidad, precio y total
            item.setCantidad(nuevaCantidad);
            item.setPrecioUnitario(dto.getPrecio());
            item.setTotal(dto.getPrecio().multiply(BigDecimal.valueOf(nuevaCantidad)));

            // Agregar los datos del usuario al ítem
            item.setFirstName(firstName);
            item.setLastName(lastName);

            // Guardar la URL de la imagen
            if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
                item.setImagenUrl(dto.getImagenes().get(0)); // Guardar la primera imagen
            } else {
                item.setImagenUrl(null); // Si no hay imágenes, poner null
            }

            // Guardar el nombre del producto
            item.setNombreProducto(dto.getNombre()); // Guardar el nombre del producto

            // Guardar los valores de las claves (valorClave1 y valorClave2)
            item.setValorClave1(dto.getValorClave1());
            item.setValorClave2(dto.getValorClave2());

            itemRepository.save(item);

        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setId(UUID.randomUUID());
            nuevoItem.setCarrito(carrito);
            nuevoItem.setCombinacionId(combinacionId);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(dto.getPrecio());
            nuevoItem.setTotal(dto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));

            // Agregar los datos del usuario al nuevo ítem
            nuevoItem.setFirstName(firstName);
            nuevoItem.setLastName(lastName);

            // Guardar la URL de la imagen
            if (dto.getImagenes() != null && !dto.getImagenes().isEmpty()) {
                nuevoItem.setImagenUrl(dto.getImagenes().get(0)); // Guardar la primera imagen
            } else {
                nuevoItem.setImagenUrl(null); // Si no hay imágenes, poner null
            }

            // Guardar el nombre del producto
            nuevoItem.setNombreProducto(dto.getNombre()); // Guardar el nombre del producto

            // Guardar los valores de las claves (valorClave1 y valorClave2)
            nuevoItem.setValorClave1(dto.getValorClave1());
            nuevoItem.setValorClave2(dto.getValorClave2());

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

    public List<ListarPocoCarrito> listarItemsPoco(UUID usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        return carrito.getItems().stream().map(item -> {
            ProductoCombinacionDTO combinacion = productoClient.obtenerCombinacionPorId(item.getCombinacionId());

            // Crear DTO con los datos del carrito
            ListarPocoCarrito dto = new ListarPocoCarrito();
            dto.setCombinacionId(item.getCombinacionId());
            dto.setCantidad(item.getCantidad());
            dto.setTotal(item.getTotal());

            // Incluir el precio del item en la respuesta
            dto.setPrecioUnitario(item.getPrecioUnitario().doubleValue());

            // Otros campos
            if (combinacion.getImagenes() != null && !combinacion.getImagenes().isEmpty()) {
                dto.setImagenUrl(combinacion.getImagenes().get(0));
            } else {
                dto.setImagenUrl(null);
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

            // Agregar la URL de la primera imagen
            if (combinacion.getImagenes() != null && !combinacion.getImagenes().isEmpty()) {
                dto.setImagenUrl(combinacion.getImagenes().get(0)); // Primera imagen
            } else {
                dto.setImagenUrl(null); // O una imagen por defecto si deseas
            }

            // Asignar los valores de las claves al DTO
            // Asumimos que la combinación tiene exactamente dos atributos (uno para cada clave)
            if (combinacion.getValorClave1() != null) {
                dto.setValorClave1(combinacion.getValorClave1());
            }
            if (combinacion.getValorClave2() != null) {
                dto.setValorClave2(combinacion.getValorClave2());
            }

            // Agregar el nombre del producto al DTO
            dto.setNombreProducto(combinacion.getNombre());

            // Agregar los datos del usuario al DTO
            dto.setFirstName(item.getFirstName());
            dto.setLastName(item.getLastName());

            return dto;
        }).toList();
    }

    @Override
    public void eliminarItemDelCarrito(UUID combinacionId, UUID usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No se encontró carrito para el usuario"));

        CarritoItem item = carritoItemRepository.findByCarritoAndCombinacionId(carrito, combinacionId)
                .orElseThrow(() -> new RuntimeException("No se encontró el item en el carrito"));

        carritoItemRepository.delete(item);
    }


















}
