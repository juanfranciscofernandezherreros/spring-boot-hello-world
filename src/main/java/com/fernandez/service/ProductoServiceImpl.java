package com.fernandez.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandez.application.port.out.ProductoPersistencePort;
import com.fernandez.dto.ProductoDto;
import com.fernandez.entity.Producto;
import com.fernandez.exception.ProductoNotFoundException;
import com.fernandez.mapper.ProductoMapper;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoPersistencePort productoPersistencePort;
    private final ProductoMapper productoMapper;
    private final String productoNotFoundPattern;

    public ProductoServiceImpl(ProductoPersistencePort productoPersistencePort,
            ProductoMapper productoMapper,
            @Value("${app.producto.not-found-pattern}") String productoNotFoundPattern) {
        this.productoPersistencePort = productoPersistencePort;
        this.productoMapper = productoMapper;
        this.productoNotFoundPattern = productoNotFoundPattern == null ? "Producto not found: %s" : productoNotFoundPattern;
    }

    @Override
    public ProductoDto create(ProductoDto producto) {
        Producto entity = productoMapper.toEntity(producto);
        return productoMapper.toDto(productoPersistencePort.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> findAll() {
        return productoMapper.toDtoList(productoPersistencePort.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDto> findById(Long id) {
        return productoPersistencePort.findById(id).map(productoMapper::toDto);
    }

    @Override
    public ProductoDto update(Long id, ProductoDto producto) {
        Producto current = productoPersistencePort.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(String.format(productoNotFoundPattern, id)));
        current.setNombre(producto.getNombre());
        current.setDescripcion(producto.getDescripcion());
        current.setPrecio(producto.getPrecio());
        current.setStock(producto.getStock());
        return productoMapper.toDto(productoPersistencePort.save(current));
    }

    @Override
    public void deleteById(Long id) {
        if (!productoPersistencePort.existsById(id)) {
            throw new ProductoNotFoundException(String.format(productoNotFoundPattern, id));
        }
        productoPersistencePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock) {
        return productoMapper.toDtoList(productoPersistencePort.findByFilters(id, nombre, descripcion, precio, stock));
    }
}
