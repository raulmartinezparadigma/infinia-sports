package com.infinia.sports.config;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.repository.jpa.ProductRepository;
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
                product1.setMage("nike_air_max_90.jpg");

                Product product2 = new Product();
                product2.setType(ProductType.SNEAKERS);
                product2.setDescription("Adidas Ultraboost");
                product2.setPrice(new BigDecimal("159.99"));
                product2.setSize("43");
                product2.setMage("adidas_ultraboost.jpg");

                Product product3 = new Product();
                product3.setType(ProductType.CLOTHING);
                product3.setDescription("Camiseta Nike Dri-FIT");
                product3.setPrice(new BigDecimal("34.99"));
                product3.setSize("M");
                product3.setMage("nike_dri_fit_shirt.jpg");

                Product product4 = new Product();
                product4.setType(ProductType.CLOTHING);
                product4.setDescription("Pantalón Adidas Training");
                product4.setPrice(new BigDecimal("49.99"));
                product4.setSize("L");
                product4.setMage("adidas_training_pants.jpg");

                Product product5 = new Product();
                product5.setType(ProductType.SUPPLEMENT);
                product5.setDescription("Proteína Whey Gold Standard");
                product5.setPrice(new BigDecimal("29.99"));
                product5.setSize("900g");
                product5.setMage("whey_gold_standard.jpg");

                // Productos sintéticos adicionales para pruebas de paginación
                Product product6 = new Product();
                product6.setType(ProductType.SNEAKERS);
                product6.setDescription("Puma Velocity Nitro 2");
                product6.setPrice(new BigDecimal("119.99"));
                product6.setSize("41");
                product6.setMage("puma_velocity_nitro_2.jpg");

                Product product7 = new Product();
                product7.setType(ProductType.SNEAKERS);
                product7.setDescription("New Balance Fresh Foam 1080");
                product7.setPrice(new BigDecimal("139.99"));
                product7.setSize("44");
                product7.setMage("nb_fresh_foam_1080.jpg");

                Product product8 = new Product();
                product8.setType(ProductType.CLOTHING);
                product8.setDescription("Shorts Under Armour Training");
                product8.setPrice(new BigDecimal("24.99"));
                product8.setSize("L");
                product8.setMage("ua_training_shorts.jpg");

                Product product9 = new Product();
                product9.setType(ProductType.CLOTHING);
                product9.setDescription("Sudadera Nike Therma-FIT");
                product9.setPrice(new BigDecimal("54.99"));
                product9.setSize("XL");
                product9.setMage("nike_therma_fit_hoodie.jpg");

                Product product10 = new Product();
                product10.setType(ProductType.SUPPLEMENT);
                product10.setDescription("Creatina Monohidrato 500g");
                product10.setPrice(new BigDecimal("19.99"));
                product10.setSize("500g");
                product10.setMage("creatina_monohidrato.jpg");

                Product product11 = new Product();
                product11.setType(ProductType.SUPPLEMENT);
                product11.setDescription("Barrita Proteica Chocolate");
                product11.setPrice(new BigDecimal("2.99"));
                product11.setSize("50g");
                product11.setMage("barrita_chocolate.jpg");

                Product product12 = new Product();
                product12.setType(ProductType.CLOTHING);
                product12.setDescription("Calcetines Deportivos Pack x3");
                product12.setPrice(new BigDecimal("9.99"));
                product12.setSize("M");
                product12.setMage("pack_calcetines.jpg");

                Product product13 = new Product();
                product13.setType(ProductType.SNEAKERS);
                product13.setDescription("Asics Gel-Kayano 28");
                product13.setPrice(new BigDecimal("149.99"));
                product13.setSize("42");
                product13.setMage("asics_gel_kayano_28.jpg");

                Product product14 = new Product();
                product14.setType(ProductType.SUPPLEMENT);
                product14.setDescription("BCAA 2:1:1 300g");
                product14.setPrice(new BigDecimal("15.99"));
                product14.setSize("300g");
                product14.setMage("bcaa_211.jpg");

                Product product15 = new Product();
                product15.setType(ProductType.CLOTHING);
                product15.setDescription("Mallas Running Mujer");
                product15.setPrice(new BigDecimal("39.99"));
                product15.setSize("S");
                product15.setMage("mallas_running_mujer.jpg");

                Product product16 = new Product();
                product16.setType(ProductType.SNEAKERS);
                product16.setDescription("Reebok Nano X2");
                product16.setPrice(new BigDecimal("129.99"));
                product16.setSize("43");
                product16.setMage("reebok_nano_x2.jpg");

                Product product17 = new Product();
                product17.setType(ProductType.SUPPLEMENT);
                product17.setDescription("Pre-entreno Energy Shot");
                product17.setPrice(new BigDecimal("3.99"));
                product17.setSize("60ml");
                product17.setMage("energy_shot.jpg");

                Product product18 = new Product();
                product18.setType(ProductType.CLOTHING);
                product18.setDescription("Camiseta Adidas Aeroready");
                product18.setPrice(new BigDecimal("29.99"));
                product18.setSize("L");
                product18.setMage("adidas_aeroready_shirt.jpg");

                Product product19 = new Product();
                product19.setType(ProductType.SNEAKERS);
                product19.setDescription("Mizuno Wave Rider 25");
                product19.setPrice(new BigDecimal("134.99"));
                product19.setSize("41");
                product19.setMage("mizuno_wave_rider_25.jpg");

                Product product20 = new Product();
                product20.setType(ProductType.SUPPLEMENT);
                product20.setDescription("Proteína Vegana 750g");
                product20.setPrice(new BigDecimal("32.99"));
                product20.setSize("750g");
                product20.setMage("proteina_vegana.jpg");

                // Guardar productos en la base de datos
                productRepository.saveAll(Arrays.asList(
                    product1, product2, product3, product4, product5,
                    product6, product7, product8, product9, product10,
                    product11, product12, product13, product14, product15,
                    product16, product17, product18, product19, product20
                ));
                
                System.out.println("Datos de prueba cargados correctamente.");
            }
        };
    }
}
