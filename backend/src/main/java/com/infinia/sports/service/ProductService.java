package com.infinia.sports.service;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de productos
 * Contiene la lógica de negocio relacionada con los productos
 */
public interface ProductService {

    /**
     * Obtiene todos los productos
     * @return list of all products
     */
    List<ProductDTO> getAllProducts();

    /**
     * Obtiene un producto por su ID
     * @param id unique product identifier
     * @return found product
     * @throws EntityNotFoundException if the product is not found
     */
    ProductDTO getProductById(UUID id);

    /**
     * Guarda un nuevo producto
     * @param product product to save
     * @return saved product
     */
    ProductDTO saveProduct(Product product);

    /**
     * Elimina un producto por su ID
     * @param id identificador único del producto
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    void deleteProductById(UUID id);

    /**
     * Actualiza un producto existente
     * @param id unique product identifier
     * @param productDetails updated product details
     * @return updated product
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    ProductDTO updateProduct(UUID id, Product productDetails);

    /**
     * Busca productos por tipo
     * @param type product type
     * @return list of products of the specified type
     */
    List<ProductDTO> getProductsByType(ProductType type);

    /**
     * Busca productos por descripción
     * @param description text to search in the product description
     * @return list of products matching the description
     */
    List<ProductDTO> getProductsByDescription(String description);

    /**
     * Busca productos por talla
     * @param size product size
     * @return list of products of the specified size
     */
    List<ProductDTO> getProductsBySize(String size);

    /**
     * Importa una lista de productos
     * @param products list of products to import
     * @return list of imported products
     */
    List<ProductDTO> importProducts(List<Product> products);
}
