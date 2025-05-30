package com.infinia.sports.config;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Clase para inicializar datos de prueba en la base de datos
 */
@Configuration
public class DataInitializer {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Carga datos de prueba al iniciar la aplicación
     * Solo se ejecuta en el perfil "dev"
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner loadData() {
        return args -> {
            // Verificar si ya existen productos
            if (productRepository.count() == 0) {
                System.out.println("Inicializando datos de prueba...");
                
                // Crear productos de prueba
                Product product1 = new Product();
                product1.setType(ProductType.SNEAKERS);
                product1.setDescription("Nike Air Max 90");
                product1.setPrice(new BigDecimal("129.99"));
                product1.setSize("42");

                Product product2 = new Product();
                product2.setType(ProductType.SNEAKERS);
                product2.setDescription("Adidas Ultraboost");
                product2.setPrice(new BigDecimal("159.99"));
                product2.setSize("43");

                Product product3 = new Product();
                product3.setType(ProductType.CLOTHING);
                product3.setDescription("Camiseta Nike Dri-FIT");
                product3.setPrice(new BigDecimal("34.99"));
                product3.setSize("M");

                Product product4 = new Product();
                product4.setType(ProductType.CLOTHING);
                product4.setDescription("Pantalón Adidas Training");
                product4.setPrice(new BigDecimal("49.99"));
                product4.setSize("L");

                Product product5 = new Product();
                product5.setType(ProductType.SUPPLEMENT);
                product5.setDescription("Proteína Whey Gold Standard");
                product5.setPrice(new BigDecimal("29.99"));
                product5.setSize("900g");

                // Guardar productos en la base de datos
                productRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5));
                
                System.out.println("Datos de prueba cargados correctamente.");
            }
        };
    }
}
