package com.infinia.sports.controller;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de productos
 * Expone los endpoints para realizar operaciones CRUD sobre productos
 */
@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Obtiene todos los productos
     * @return lista de productos
     */
    @Operation(summary = "Obtener todos los productos", description = "Devuelve una lista de todos los productos disponibles. Se puede filtrar por tipo, descripción o talla.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos encontrados",
                content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Product.class, description = "Incluye el campo 'mage' para la imagen del producto"))})
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @Parameter(description = "Tipo de producto (SNEAKERS, CLOTHING, SUPPLEMENT)") @RequestParam(required = false) ProductType tipo,
            @Parameter(description = "Texto a buscar en la descripción del producto") @RequestParam(required = false) String descripcion,
            @Parameter(description = "Talla del producto") @RequestParam(required = false) String talla) {
        
        List<Product> products;
        
        if (tipo != null) {
            products = productService.getProductsByType(tipo);
        } else if (descripcion != null) {
            products = productService.getProductsByDescription(descripcion);
        } else if (talla != null) {
            products = productService.getProductsBySize(talla);
        } else {
            products = productService.getAllProducts();
        }
        
        return ResponseEntity.ok(products);
    }

    /**
     * Obtiene un producto por su ID
     * @param id identificador único del producto
     * @return el producto encontrado
     */
    @Operation(summary = "Obtener un producto por ID", description = "Devuelve un producto específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
                content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Product.class, description = "Incluye el campo 'mage' para la imagen del producto"))}),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@Parameter(description = "ID del producto") @PathVariable UUID id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo producto
     * @param product producto a crear
     * @return el producto creado
     */
    @Operation(summary = "Crear un nuevo producto", description = "Crea un nuevo producto en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado correctamente",
                content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Product.class, description = "Incluye el campo 'mage' para la imagen del producto"))}),
        @ApiResponse(responseCode = "400", description = "Datos de producto inválidos",
                content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@Parameter(description = "Datos del producto a crear") @Valid @RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * Actualiza un producto existente
     * @param id identificador único del producto
     * @param product detalles actualizados del producto
     * @return el producto actualizado
     */
    @Operation(summary = "Actualizar un producto existente", description = "Actualiza los datos de un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Product.class, description = "Incluye el campo 'mage' para la imagen del producto"))}),
        @ApiResponse(responseCode = "400", description = "Datos de producto inválidos",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID del producto a actualizar") @PathVariable UUID id, 
            @Parameter(description = "Datos actualizados del producto") @Valid @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un producto por su ID
     * @param id identificador único del producto
     * @return respuesta sin contenido
     */
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "ID del producto a eliminar") @PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Importa una lista de productos
     * @param products lista de productos a importar
     * @return lista de productos importados
     */
    @Operation(summary = "Importar productos", description = "Permite importar múltiples productos en una sola operación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Productos importados correctamente",
                content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Product.class, description = "Incluye el campo 'mage' para la imagen del producto"))}),
        @ApiResponse(responseCode = "400", description = "Datos de productos inválidos",
                content = @Content)
    })
    @PostMapping("/importar")
    public ResponseEntity<List<Product>> importProducts(@Parameter(description = "Lista de productos a importar") @Valid @RequestBody List<Product> products) {
        List<Product> importedProducts = productService.importProducts(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(importedProducts);
    }
}
