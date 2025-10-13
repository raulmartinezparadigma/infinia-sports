package com.infinia.sports.service.impl;

import com.infinia.sports.kafka.dto.ProductKafkaMessage;
import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.mapper.mapstruct.ProductMapperMS;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.ImageStorageService;
import com.infinia.sports.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ProductMapperMS productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ImageStorageService imageStorageService, ProductMapperMS productMapper) {
        this.productRepository = productRepository;
        this.imageStorageService = imageStorageService;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productMapper.toDTOList(productRepository.findAll());
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO saveProduct(Product product) {
        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
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
        return productMapper.toDTO(updated);
    }

    @Override
    public List<ProductDTO> getProductsByType(ProductType type) {
        return productMapper.toDTOList(productRepository.findByType(type));
    }

    @Override
    public List<ProductDTO> getProductsByDescription(String description) {
        return productMapper.toDTOList(productRepository.findByDescriptionContainingIgnoreCase(description));
    }

    @Override
    public List<ProductDTO> getProductsBySize(String size) {
        return productMapper.toDTOList(productRepository.findBySize(size));
    }

    @Override
    @Transactional
    public List<ProductDTO> importProducts(List<Product> products) {
        List<Product> imported = productRepository.saveAll(products);
        return productMapper.toDTOList(imported);
    }

    @Override
    @Transactional
    public void createProductFromKafka(ProductKafkaMessage message) {
        try {
            // Usamos la descripción del producto para generar un nombre de fichero descriptivo
            String localImageUrl = imageStorageService.storeImage(message.getImageUrl(), message.getDescription());

            Product product = new Product();
            // Mapeo correcto desde ProductKafkaMessage a la entidad Product
            product.setId(UUID.fromString(message.getId()));
            product.setSkuId(message.getSkuId());
            product.setType(ProductType.valueOf(message.getType().toUpperCase()));
            product.setDescription(message.getDescription());
            product.setPrice(message.getPrice());
            product.setSize(message.getSize());
            product.setImageUrl(localImageUrl != null ? localImageUrl : message.getImageUrl()); // Usar la URL local si existe

            logger.debug("Preparado para guardar el siguiente producto en la base de datos: {}", product);
            productRepository.save(product);
            logger.info("Producto con SKU {} procesado y guardado desde Kafka.", product.getSkuId());

        } catch (IOException e) {
            logger.error("Error al procesar la imagen para el producto desde Kafka: {}. El producto no se guardará.", message.getSkuId(), e);
            // No bloqueamos el topic, solo registramos el error
        } catch (Exception e) {
            logger.error("Error inesperado al procesar producto desde Kafka con SKU: {}.", message.getSkuId(), e);
        }
    }
}
