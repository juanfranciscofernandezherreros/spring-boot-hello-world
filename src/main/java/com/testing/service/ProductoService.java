package com.testing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.testing.entity.Producto;

public interface ProductoService {

    Producto create(Producto producto);

    List<Producto> findAll();

    Optional<Producto> findById(Long id);

    Producto update(Long id, Producto producto);

    void deleteById(Long id);

    List<Producto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock);
}
