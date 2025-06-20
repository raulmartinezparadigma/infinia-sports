package com.infinia.sports.config;

import com.infinia.sports.model.Product;
import com.infinia.sports.model.ProductType;
import com.infinia.sports.kafka.ProductProducer;
import com.infinia.sports.kafka.dto.ProductKafkaMessage;

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

    private final ProductProducer productProducer;

    public DataInitializer(ProductProducer productProducer) {
        this.productProducer = productProducer;
    }

    /**
     * Carga datos de prueba al iniciar la aplicación
     * Solo se ejecuta en el perfil "dev"
     */
    @Bean
    @Profile("dev")
    public CommandLineRunner loadData() {
        return args -> {
            // No se verifica si existen productos, ya que la carga se realiza vía Kafka
            System.out.println("Inicializando datos de prueba (Kafka)...");
            
                // Crear productos de prueba
                Product product1 = new Product();
                product1.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000001")); // UUID correlativo 1 para Nike Air Max 90
                product1.setType(ProductType.SNEAKERS);
                product1.setSkuId("000000000000000001");
                product1.setDescription("Nike Air Max 90");
                product1.setPrice(new BigDecimal("129.99"));
                product1.setSize("42");
                product1.setImageUrl("nike_air_max_90.jpg");

                Product product2 = new Product();
                product2.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000002")); // UUID correlativo 2 para Adidas Ultraboost
                product2.setType(ProductType.SNEAKERS);
                product2.setSkuId("000000000000000002");
                product2.setDescription("Adidas Ultraboost");
                product2.setPrice(new BigDecimal("159.99"));
                product2.setSize("43");
                product2.setImageUrl("adidas_ultraboost.jpg");

                Product product3 = new Product();
                product3.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000003")); // UUID correlativo 3 para Camiseta Nike Dri-FIT
                product3.setType(ProductType.CLOTHING);
                product3.setSkuId("000000000000000003");
                product3.setDescription("Camiseta Nike Dri-FIT");
                product3.setPrice(new BigDecimal("34.99"));
                product3.setSize("M");
                product3.setImageUrl("nike_dri_fit_shirt.jpg");

                Product product4 = new Product();
                product4.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000004")); // UUID correlativo 4 para Pantalón Adidas Training
                product4.setType(ProductType.CLOTHING);
                product4.setSkuId("000000000000000004");
                product4.setDescription("Pantalón Adidas Training");
                product4.setPrice(new BigDecimal("49.99"));
                product4.setSize("L");
                product4.setImageUrl("adidas_training_pants.jpg");

                Product product5 = new Product();
                product5.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000005")); // UUID correlativo 5 para Proteína Whey Gold Standard
                product5.setType(ProductType.SUPPLEMENT);
                product5.setSkuId("000000000000000005");
                product5.setDescription("Proteína Whey Gold Standard");
                product5.setPrice(new BigDecimal("29.99"));
                product5.setSize("900g");
                product5.setImageUrl("whey_gold_standard.jpg");

                // Productos sintéticos adicionales para pruebas de paginación
                Product product6 = new Product();
                product6.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000006")); // UUID correlativo 6 para Puma Velocity Nitro 2
                product6.setType(ProductType.SNEAKERS);
                product6.setSkuId("000000000000000006");
                product6.setDescription("Puma Velocity Nitro 2");
                product6.setPrice(new BigDecimal("119.99"));
                product6.setSize("41");
                product6.setImageUrl("puma_velocity_nitro_2.jpg");

                Product product7 = new Product();
                product7.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000007")); // UUID correlativo 7 para New Balance Fresh Foam 1080
                product7.setType(ProductType.SNEAKERS);
                product7.setSkuId("000000000000000007");
                product7.setDescription("New Balance Fresh Foam 1080");
                product7.setPrice(new BigDecimal("139.99"));
                product7.setSize("44");
                product7.setImageUrl("nb_fresh_foam_1080.jpg");

                Product product8 = new Product();
                product8.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000008")); // UUID correlativo 8 para Shorts Under Armour Training
                product8.setType(ProductType.CLOTHING);
                product8.setSkuId("000000000000000008");
                product8.setDescription("Shorts Under Armour Training");
                product8.setPrice(new BigDecimal("24.99"));
                product8.setSize("L");
                product8.setImageUrl("ua_training_shorts.jpg");

                Product product9 = new Product();
                product9.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000009")); // UUID correlativo 9 para Sudadera Nike Therma-FIT
                product9.setType(ProductType.CLOTHING);
                product9.setSkuId("000000000000000009");
                product9.setDescription("Sudadera Nike Therma-FIT");
                product9.setPrice(new BigDecimal("54.99"));
                product9.setSize("XL");
                product9.setImageUrl("nike_therma_fit_hoodie.jpg");

                Product product10 = new Product();
                product10.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000010")); // UUID correlativo 10 para Creatina Monohidrato 500g
                product10.setType(ProductType.SUPPLEMENT);
                product10.setSkuId("000000000000000010");
                product10.setDescription("Creatina Monohidrato 500g");
                product10.setPrice(new BigDecimal("19.99"));
                product10.setSize("500g");
                product10.setImageUrl("creatina_monohidrato.jpg");

                Product product11 = new Product();
                product11.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000011")); // UUID correlativo 11 para Barrita Proteica Chocolate
                product11.setType(ProductType.SUPPLEMENT);
                product11.setSkuId("000000000000000011");
                product11.setDescription("Barrita Proteica Chocolate");
                product11.setPrice(new BigDecimal("2.99"));
                product11.setSize("50g");
                product11.setImageUrl("barrita_chocolate.jpg");

                Product product12 = new Product();
                product12.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000012")); // UUID correlativo 12 para Calcetines Deportivos Pack x3
                product12.setType(ProductType.CLOTHING);
                product12.setSkuId("000000000000000012");
                product12.setDescription("Calcetines Deportivos Pack x3");
                product12.setPrice(new BigDecimal("9.99"));
                product12.setSize("M");
                product12.setImageUrl("pack_calcetines.jpg");

                Product product13 = new Product();
                product13.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000013")); // UUID correlativo 13 para Asics Gel-Kayano 28
                product13.setType(ProductType.SNEAKERS);
                product13.setSkuId("000000000000000013");
                product13.setDescription("Asics Gel-Kayano 28");
                product13.setPrice(new BigDecimal("149.99"));
                product13.setSize("42");
                product13.setImageUrl("asics_gel_kayano_28.jpg");

                Product product14 = new Product();
                product14.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000014")); // UUID correlativo 14 para BCAA 2:1:1 300g
                product14.setType(ProductType.SUPPLEMENT);
                product14.setSkuId("000000000000000014");
                product14.setDescription("BCAA 2:1:1 300g");
                product14.setPrice(new BigDecimal("15.99"));
                product14.setSize("300g");
                product14.setImageUrl("bcaa_211.jpg");

                Product product15 = new Product();
                product15.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000015")); // UUID correlativo 15 para Mallas Running Mujer
                product15.setType(ProductType.CLOTHING);
                product15.setSkuId("000000000000000015");
                product15.setDescription("Mallas Running Mujer");
                product15.setPrice(new BigDecimal("39.99"));
                product15.setSize("S");
                product15.setImageUrl("mallas_running_mujer.jpg");

                Product product16 = new Product();
                product16.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000016")); // UUID correlativo 16 para Reebok Nano X2
                product16.setType(ProductType.SNEAKERS);
                product16.setSkuId("000000000000000016");
                product16.setDescription("Reebok Nano X2");
                product16.setPrice(new BigDecimal("129.99"));
                product16.setSize("43");
                product16.setImageUrl("reebok_nano_x2.jpg");

                Product product17 = new Product();
                product17.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000017")); // UUID correlativo 17 para Pre-entreno Energy Shot
                product17.setType(ProductType.SUPPLEMENT);
                product17.setSkuId("000000000000000017");
                product17.setDescription("Pre-entreno Energy Shot");
                product17.setPrice(new BigDecimal("3.99"));
                product17.setSize("60ml");
                product17.setImageUrl("energy_shot.jpg");

                Product product18 = new Product();
                product18.setId(java.util.UUID.fromString("00000000-0000-0000-0000-000000000018")); // UUID correlativo 18 para Camiseta Adidas Aeroready
                product18.setType(ProductType.CLOTHING);
                product18.setSkuId("000000000000000018");
                product18.setDescription("Camiseta Adidas Aeroready");
                product18.setPrice(new BigDecimal("29.99"));
                product18.setSize("L");
                product18.setImageUrl("adidas_aeroready_shirt.jpg");

                Product product19 = new Product();
                product19.setId(java.util.UUID.randomUUID()); // Asignar UUID único
                product19.setSkuId("000000000000000019");
                product19.setType(ProductType.SNEAKERS);
                product19.setDescription("Mizuno Wave Rider 25");
                product19.setPrice(new BigDecimal("134.99"));
                product19.setSize("41");
                product19.setImageUrl("mizuno_wave_rider_25.jpg");

                Product product20 = new Product();
                product20.setId(java.util.UUID.randomUUID()); // Asignar UUID único
                product20.setSkuId("000000000000000020");
                product20.setType(ProductType.SUPPLEMENT);
                product20.setDescription("Proteína Vegana 750g");
                product20.setPrice(new BigDecimal("32.99"));
                product20.setSize("750g");
                product20.setImageUrl("proteina_vegana.jpg");

                // Enviar productos a Kafka uno a uno
                for (Product product : Arrays.asList(
                        product1, product2, product3, product4, product5,
                        product6, product7, product8, product9, product10,
                        product11, product12, product13, product14, product15,
                        product16, product17, product18, product19, product20
                )) {
                    ProductKafkaMessage message = new ProductKafkaMessage(
                        product.getId() != null ? product.getId().toString() : null,
                        product.getSkuId(),
                        product.getType().name(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getSize(),
                        product.getImageUrl()
                    );
                    productProducer.sendProduct(message);
                }
                System.out.println("Datos de prueba enviados a Kafka correctamente.");
        };
    }
}
