package com.infinia.sports.mail;

import com.infinia.sports.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderMailTemplateUtilTest {

    private Order order;
    private Order.ShippingGroup shippingGroup;
    private Order.LineItem lineItem;
    private Order.Address address;
    private Order.PriceInfo priceInfo;

    @BeforeEach
    void setUp() {
        // Create a sample order for testing
        order = new Order();
        order.setOrderId("ORD-12345");
        order.setSubmitDate(LocalDateTime.now());

        // Create shipping group
        shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId("SG-1");

        // Create line item
        lineItem = new Order.LineItem();
        lineItem.setId("LI-1");
        lineItem.setProductName("Zapatillas Running");
        lineItem.setQuantity(2);
        lineItem.setUnitPrice(new BigDecimal("79.99"));

        // Add line item to shipping group
        shippingGroup.setLineItems(Collections.singletonList(lineItem));

        // Add shipping group to order
        order.setShippingGroups(Collections.singletonList(shippingGroup));

        // Create address
        address = new Order.Address();
        address.setFirstName("Juan");
        address.setLastName("Pérez");
        address.setAddressLine1("Calle Principal 123");
        address.setAddressLine2("Apartamento 4B");
        address.setCity("Madrid");
        address.setState("Madrid");
        address.setPostalCode("28001");
        address.setCountry("España");

        // Set address to order
        order.setShippingAddress(address);

        // Create price info
        priceInfo = new Order.PriceInfo();
        priceInfo.setTotal(new BigDecimal("159.98"));
        
        // Set price info to order
        order.setPriceInfo(priceInfo);
    }

    @Test
    void testGenerateOrderSummaryHtml_Success(@TempDir Path tempDir) throws IOException {
        // Given
        String templateContent = "<!DOCTYPE html><html><body>" +
                "<h1>Resumen de Pedido</h1>" +
                "<p>Hola {{customerName}},</p>" +
                "<p>Tu pedido {{orderId}} del {{orderDate}} ha sido confirmado.</p>" +
                "<table>{{orderLines}}</table>" +
                "<p>Total: {{orderTotal}}</p>" +
                "<p>Dirección de envío: {{shippingAddress}}</p>" +
                "</body></html>";
        
        Path templatePath = tempDir.resolve("order-summary.html");
        Files.writeString(templatePath, templateContent);
        
        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            
            mockedPaths.when(() -> Paths.get("src/main/resources/templates/order-summary.html"))
                    .thenReturn(templatePath);
            
            mockedFiles.when(() -> Files.readString(templatePath))
                    .thenReturn(templateContent);
            
            // When
            String result = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            
            // Then
            assertNotNull(result);
            assertTrue(result.contains("Juan Pérez"));
            assertTrue(result.contains("ORD-12345"));
            assertTrue(result.contains("Zapatillas Running"));
            assertTrue(result.contains("159.98€"));
            assertTrue(result.contains("Calle Principal 123 Apartamento 4B, Madrid, Madrid, 28001, España"));
        }
    }

    @Test
    void testGenerateOrderSummaryHtml_WithNullAddressLine2() throws IOException {
        // Given
        address.setAddressLine2(null);
        
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(Mockito.any()))
                    .thenReturn("<p>{{shippingAddress}}</p>");
            
            // When
            String result = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            
            // Then
            assertNotNull(result);
            assertTrue(result.contains("Calle Principal 123"));
            assertFalse(result.contains("Apartamento 4B"));
        }
    }

    @Test
    void testGenerateOrderSummaryHtml_Exception() {
        // Given
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(Mockito.any()))
                    .thenThrow(new IOException("File not found"));
            
            // When
            String result = OrderMailTemplateUtil.generateOrderSummaryHtml(order);
            
            // Then
            assertEquals("Gracias por tu compra. Pedido: ORD-12345", result);
        }
    }
}
