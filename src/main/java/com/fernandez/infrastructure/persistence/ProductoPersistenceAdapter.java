package com.fernandez.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fernandez.application.port.out.ProductoPersistencePort;
import com.fernandez.entity.Producto;
import com.fernandez.repository.ProductoRepository;

@Component
public class ProductoPersistenceAdapter implements ProductoPersistencePort {

    private final ProductoRepository productoRepository;

    public ProductoPersistenceAdapter(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productoRepository.existsById(id);
    }

    @Override
    public List<Producto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock) {
        return productoRepository.findByFilters(id, nombre, descripcion, precio, stock);
    }
}
