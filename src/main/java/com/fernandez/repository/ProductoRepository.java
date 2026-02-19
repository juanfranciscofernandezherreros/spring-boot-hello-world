package com.fernandez.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fernandez.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p "
            + "WHERE (:id IS NULL OR p.id = :id) "
            + "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) "
            + "AND (:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) "
            + "AND (:precio IS NULL OR p.precio = :precio) "
            + "AND (:stock IS NULL OR p.stock = :stock)")
    List<Producto> findByFilters(@Param("id") Long id,
            @Param("nombre") String nombre,
            @Param("descripcion") String descripcion,
            @Param("precio") BigDecimal precio,
            @Param("stock") Integer stock);
}
