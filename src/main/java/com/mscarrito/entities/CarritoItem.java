package com.mscarrito.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "carrito_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {

    @Id
    @Column(name = "id_item")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(name = "combinacion_id", nullable = false)
    private UUID combinacionId;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal total;

    // Nuevos campos para almacenar firstName y lastName
    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    // Nuevo campo para almacenar la URL de la imagen del producto
    @Column(name = "imagen_url", nullable = true)
    private String imagenUrl;

    // Nuevo campo para almacenar el nombre del producto
    @Column(name = "nombre_producto", nullable = true)
    private String nombreProducto;

    // Campos para almacenar los valores de las claves
    @Column(name = "valor_clave_1", nullable = true)
    private String valorClave1;  // Valor de la primera clave

    @Column(name = "valor_clave_2", nullable = true)
    private String valorClave2;  // Valor de la segunda clave
}
