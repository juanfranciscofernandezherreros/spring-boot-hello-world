package com.fernandez.application.port.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fernandez.entity.Producto;

public interface ProductoPersistencePort {

    Producto save(Producto producto);

    List<Producto> findAll();

    Optional<Producto> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Producto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock);
}
