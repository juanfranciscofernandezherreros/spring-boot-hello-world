package com.fernandez.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.fernandez.dto.ProductoDto;
import com.fernandez.entity.Producto;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    ProductoDto toDto(Producto producto);

    Producto toEntity(ProductoDto productoDto);

    List<ProductoDto> toDtoList(List<Producto> productos);
}
