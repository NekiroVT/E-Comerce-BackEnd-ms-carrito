package com.mscarrito.repository;

import com.mscarrito.entities.Carrito;
import com.mscarrito.entities.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, UUID> {

    List<CarritoItem> findByCarrito(Carrito carrito);

    Optional<CarritoItem> findByCarritoAndCombinacionId(Carrito carrito, UUID combinacionId);

    void deleteByCarrito(Carrito carrito);
}
