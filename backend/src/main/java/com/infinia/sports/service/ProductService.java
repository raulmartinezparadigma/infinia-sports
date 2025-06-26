package com.infinia.sports.service;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.mapper.ProductMapper;
import com.infinia.sports.repository.jpa.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para la gestión de productos
 * Contiene la lógica de negocio relacionada con los productos
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Obtiene todos los productos
     * @return list of all products
     */
    public List<ProductDTO> getAllProducts() {
        return ProductMapper.toDTOList(productRepository.findAll());
    }

    /**
     * Obtiene un producto por su ID
     * @param id unique product identifier
     * @return found product
     * @throws EntityNotFoundException if the product is not found
     */
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return ProductMapper.toDTO(product);
    }

    /**
     * Guarda un nuevo producto
     * @param product product to save
     * @return saved product
     */
    @Transactional
    public ProductDTO saveProduct(Product product) {
        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
    }

    /**
     * Actualiza un producto existente
     * @param id unique product identifier
     * @param productDetails updated product details
     * @return updated product
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    @Transactional
    public ProductDTO updateProduct(UUID id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        product.setType(productDetails.getType());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setSize(productDetails.getSize());
        product.setImageUrl(productDetails.getImageUrl());
        Product updated = productRepository.save(product);
        return ProductMapper.toDTO(updated);
    }

    /**
     * Elimina un producto por su ID
     * @param id identificador único del producto
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Busca productos por tipo
     * @param type product type
     * @return list of products of the specified type
     */
    public List<ProductDTO> getProductsByType(ProductType type) {
        return ProductMapper.toDTOList(productRepository.findByType(type));
    }

    /**
     * Busca productos por descripción
     * @param description text to search in the product description
     * @return list of products matching the description
     */
    public List<ProductDTO> getProductsByDescription(String description) {
        return ProductMapper.toDTOList(productRepository.findByDescriptionContainingIgnoreCase(description));
    }

    /**
     * Busca productos por talla
     * @param size product size
     * @return list of products of the specified size
     */
    public List<ProductDTO> getProductsBySize(String size) {
        return ProductMapper.toDTOList(productRepository.findBySize(size));
    }

    /**
     * Importa una lista de productos
     * @param products list of products to import
     * @return list of imported products
     */
    @Transactional
    public List<ProductDTO> importProducts(List<Product> products) {
        List<Product> imported = productRepository.saveAll(products);
        return ProductMapper.toDTOList(imported);
    }
}
