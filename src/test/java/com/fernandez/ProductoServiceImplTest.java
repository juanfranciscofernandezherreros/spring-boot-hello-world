package com.fernandez;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.fernandez.application.port.out.ProductoPersistencePort;
import com.fernandez.dto.ProductoDto;
import com.fernandez.entity.Producto;
import com.fernandez.exception.ProductoNotFoundException;
import com.fernandez.mapper.ProductoMapper;
import com.fernandez.service.ProductoServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoPersistencePort productoPersistencePort;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Test
    public void updateShouldThrowProductoNotFoundExceptionWhenIdDoesNotExist() {
        when(productoPersistencePort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.update(99L, ProductoDto.builder().build()))
                .isInstanceOf(ProductoNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowProductoNotFoundExceptionWhenIdDoesNotExist() {
        when(productoPersistencePort.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.deleteById(10L))
                .isInstanceOf(ProductoNotFoundException.class);
        verify(productoPersistencePort, never()).deleteById(10L);
    }

    @Test
    public void deleteShouldCallRepositoryWhenIdExists() {
        when(productoPersistencePort.existsById(1L)).thenReturn(true);

        productoService.deleteById(1L);

        verify(productoPersistencePort).deleteById(1L);
    }

    @Test
    public void createShouldMapAndSaveProducto() {
        ProductoDto dto = ProductoDto.builder()
                .nombre("Teclado")
                .descripcion("Mecanico")
                .precio(new BigDecimal("99.99"))
                .stock(7)
                .build();
        Producto entity = Producto.builder()
                .nombre("Teclado")
                .descripcion("Mecanico")
                .precio(new BigDecimal("99.99"))
                .stock(7)
                .build();
        Producto saved = Producto.builder()
                .id(1L)
                .nombre("Teclado")
                .descripcion("Mecanico")
                .precio(new BigDecimal("99.99"))
                .stock(7)
                .build();
        ProductoDto savedDto = ProductoDto.builder().id(1L).nombre("Teclado").build();

        when(productoMapper.toEntity(dto)).thenReturn(entity);
        when(productoPersistencePort.save(entity)).thenReturn(saved);
        when(productoMapper.toDto(saved)).thenReturn(savedDto);

        productoService.create(dto);

        verify(productoPersistencePort).save(entity);
        verify(productoMapper).toDto(saved);
    }
}
