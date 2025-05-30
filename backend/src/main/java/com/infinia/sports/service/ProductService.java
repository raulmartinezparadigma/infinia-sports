package com.infinia.sports.service;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Obtiene todos los productos
     * @return lista de todos los productos
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Obtiene un producto por su ID
     * @param id identificador único del producto
     * @return el producto encontrado
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
    }

    /**
     * Guarda un nuevo producto
     * @param product producto a guardar
     * @return el producto guardado
     */
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Actualiza un producto existente
     * @param id identificador único del producto
     * @param productDetails detalles actualizados del producto
     * @return el producto actualizado
     * @throws EntityNotFoundException si no se encuentra el producto
     */
    @Transactional
    public Product updateProduct(UUID id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setType(productDetails.getType());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setSize(productDetails.getSize());
        
        return productRepository.save(product);
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
     * @param type tipo de producto
     * @return lista de productos del tipo especificado
     */
    public List<Product> getProductsByType(ProductType type) {
        return productRepository.findByType(type);
    }

    /**
     * Busca productos por descripción
     * @param description texto a buscar en la descripción
     * @return lista de productos que coinciden con la descripción
     */
    public List<Product> getProductsByDescription(String description) {
        return productRepository.findByDescriptionContainingIgnoreCase(description);
    }

    /**
     * Busca productos por talla
     * @param size talla del producto
     * @return lista de productos de la talla especificada
     */
    public List<Product> getProductsBySize(String size) {
        return productRepository.findBySize(size);
    }

    /**
     * Importa una lista de productos
     * @param products lista de productos a importar
     * @return lista de productos importados
     */
    @Transactional
    public List<Product> importProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }
}
