package com.fernandez.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.fernandez.dto.ProductoDto;

public interface ProductoUseCase {

    ProductoDto create(ProductoDto producto);

    List<ProductoDto> findAll();

    Optional<ProductoDto> findById(Long id);

    ProductoDto update(Long id, ProductoDto producto);

    void deleteById(Long id);

    List<ProductoDto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock);
}
