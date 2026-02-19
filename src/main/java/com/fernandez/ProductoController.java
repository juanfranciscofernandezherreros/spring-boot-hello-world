package com.fernandez;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandez.application.port.in.ProductoUseCase;
import com.fernandez.constants.UrlConstants;
import com.fernandez.dto.ProductoDto;

@RestController
@RequestMapping(UrlConstants.API_PRODUCTOS)
public class ProductoController {

    private final ProductoUseCase productoService;

    public ProductoController(ProductoUseCase productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDto> create(@RequestBody ProductoDto producto) {
        return new ResponseEntity<>(productoService.create(producto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProductoDto> findAll(@RequestParam(required = false) Long id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) Integer stock) {
        if (id == null && nombre == null && descripcion == null && precio == null && stock == null) {
            return productoService.findAll();
        }
        return productoService.findByFilters(id, nombre, descripcion, precio, stock);
    }

    @GetMapping(UrlConstants.API_PRODUCTO_BY_ID)
    public ResponseEntity<ProductoDto> findById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(UrlConstants.API_PRODUCTO_BY_ID)
    public ResponseEntity<ProductoDto> update(@PathVariable Long id, @RequestBody ProductoDto producto) {
        return ResponseEntity.ok(productoService.update(id, producto));
    }

    @DeleteMapping(UrlConstants.API_PRODUCTO_BY_ID)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
