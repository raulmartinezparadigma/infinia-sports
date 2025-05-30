package com.infinia.sports.repository;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad Product
 * Proporciona métodos para acceder a la base de datos
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    
    /**
     * Busca productos por tipo
     * @param type tipo de producto
     * @return lista de productos del tipo especificado
     */
    List<Product> findByType(ProductType type);
    
    /**
     * Busca productos que contengan la descripción especificada
     * @param description texto a buscar en la descripción
     * @return lista de productos que coinciden con la descripción
     */
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Busca productos por talla
     * @param size talla del producto
     * @return lista de productos de la talla especificada
     */
    List<Product> findBySize(String size);
}
