package com.testing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testing.entity.Producto;
import com.testing.repository.ProductoRepository;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto create(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto update(Long id, Producto producto) {
        Producto current = productoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto not found: " + id));
        current.setNombre(producto.getNombre());
        current.setDescripcion(producto.getDescripcion());
        current.setPrecio(producto.getPrecio());
        current.setStock(producto.getStock());
        return productoRepository.save(current);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByFilters(Long id, String nombre, String descripcion, BigDecimal precio, Integer stock) {
        return productoRepository.findByFilters(id, nombre, descripcion, precio, stock);
    }
}
