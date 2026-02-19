package com.fernandez;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fernandez.application.port.in.ProductoUseCase;
import com.fernandez.dto.ProductoDto;
import com.fernandez.exception.ProductoNotFoundException;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductoController.class)
public class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoUseCase productoUseCase;

    @Test
    public void shouldCreateProducto() throws Exception {
        ProductoDto response = ProductoDto.builder().id(1L).nombre("Laptop").build();
        when(productoUseCase.create(any(ProductoDto.class))).thenReturn(response);

        ProductoDto request = ProductoDto.builder()
                .nombre("Laptop")
                .descripcion("Ultrabook")
                .precio(new BigDecimal("1299.99"))
                .stock(5)
                .build();

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    public void shouldReturnFilteredProductos() throws Exception {
        ProductoDto response = ProductoDto.builder().id(1L).nombre("Laptop").build();
        when(productoUseCase.findByFilters(eq(1L), eq("lap"), eq("ultra"), eq(new BigDecimal("1299.99")), eq(5)))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/productos")
                .param("id", "1")
                .param("nombre", "lap")
                .param("descripcion", "ultra")
                .param("precio", "1299.99")
                .param("stock", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void shouldReturnProductoById() throws Exception {
        when(productoUseCase.findById(1L)).thenReturn(Optional.of(ProductoDto.builder().id(1L).nombre("Laptop").build()));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingUnknownProducto() throws Exception {
        doThrow(new ProductoNotFoundException(77L)).when(productoUseCase).update(eq(77L), any(ProductoDto.class));

        mockMvc.perform(put("/api/productos/77")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"X\",\"descripcion\":\"Y\",\"precio\":1.0,\"stock\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void shouldDeleteProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
