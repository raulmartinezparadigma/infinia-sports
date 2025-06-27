package com.infinia.sports.service.impl;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.mapper.ProductMapper;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return ProductMapper.toDTOList(productRepository.findAll());
    }

    @Override
    public ProductDTO getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + id));
        return ProductMapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductDTO saveProduct(Product product) {
        Product saved = productRepository.save(product);
        return ProductMapper.toDTO(saved);
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
        return ProductMapper.toDTO(updated);
    }

    @Override
    public List<ProductDTO> getProductsByType(ProductType type) {
        return ProductMapper.toDTOList(productRepository.findByType(type));
    }

    @Override
    public List<ProductDTO> getProductsByDescription(String description) {
        return ProductMapper.toDTOList(productRepository.findByDescriptionContainingIgnoreCase(description));
    }

    @Override
    public List<ProductDTO> getProductsBySize(String size) {
        return ProductMapper.toDTOList(productRepository.findBySize(size));
    }

    @Override
    @Transactional
    public List<ProductDTO> importProducts(List<Product> products) {
        List<Product> imported = productRepository.saveAll(products);
        return ProductMapper.toDTOList(imported);
    }
}
