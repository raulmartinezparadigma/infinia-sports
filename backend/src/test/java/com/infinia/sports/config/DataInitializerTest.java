package com.infinia.sports.config;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.dto.ProductDTO;
import com.infinia.sports.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void whenDatabaseIsEmpty_thenProductsAreImported() throws Exception {
        // Given: El servicio devuelve una lista vacía, simulando una BD sin productos.
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // When: Se ejecuta el CommandLineRunner devuelto por el inicializador.
        CommandLineRunner runner = dataInitializer.loadData();
        runner.run();

        // Then: Se verifica que se llamó al método para importar productos exactamente una vez.
        verify(productService, times(1)).getAllProducts();
        verify(productService, times(1)).importProducts(any(List.class));
    }

    @Test
    void whenDatabaseIsNotEmpty_thenProductsAreNotImported() throws Exception {
        // Given: El servicio devuelve una lista con un producto, simulando una BD con datos.
        when(productService.getAllProducts()).thenReturn(List.of(new ProductDTO()));

        // When: Se ejecuta el CommandLineRunner.
        CommandLineRunner runner = dataInitializer.loadData();
        runner.run();

        // Then: Se verifica que el método para importar productos NUNCA fue llamado.
        verify(productService, times(1)).getAllProducts();
        verify(productService, never()).importProducts(any(List.class));
    }
}
