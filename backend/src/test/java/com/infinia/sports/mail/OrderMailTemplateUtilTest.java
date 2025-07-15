package com.infinia.sports.mail;

import com.infinia.sports.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderMailTemplateUtilTest {

    private Order order;
    private Order.Address address;
    private Order.PriceInfo priceInfo;

    @BeforeEach
    void setUp() {
        // Create a sample order for testing
        order = new Order();
        order.setOrderId("ORD-12345");
        order.setSubmitDate(LocalDateTime.now());

        // Create line item
        Order.LineItem lineItem = new Order.LineItem();
        lineItem.setId("LI-1");
        lineItem.setProductName("Zapatillas Running");
        lineItem.setQuantity(2);
        lineItem.setUnitPrice(new BigDecimal("79.99"));

        // Create shipping group
        Order.ShippingGroup shippingGroup = new Order.ShippingGroup();
        shippingGroup.setId("SG-1");
        shippingGroup.setLineItems(Collections.singletonList(lineItem));
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
        order.setShippingAddress(address);

        // Create price info with full breakdown
        priceInfo = new Order.PriceInfo();
        priceInfo.setSubtotal(new BigDecimal("159.98"));
        priceInfo.setShippingCost(new BigDecimal("4.99"));
        priceInfo.setTax(new BigDecimal("34.65")); // (159.98 + 4.99) * 0.21
        priceInfo.setTotal(new BigDecimal("199.62"));
        order.setPriceInfo(priceInfo);
    }

    @Test
    void testGenerateOrderSummaryHtml_Success() throws IOException {
        // Given
        String templateContent = "<!DOCTYPE html><html><body>"
                + "<p>Hola {{customerName}},</p>"
                + "<p>Tu pedido {{orderId}} del {{orderDate}} ha sido confirmado.</p>"
                + "<table>{{orderLines}}</table>"
                + "<table>"
                + "<tr><td>Subtotal:</td><td>{{orderSubtotal}}</td></tr>"
                + "<tr><td>Gastos de envío:</td><td>{{orderShipping}}</td></tr>"
                + "<tr><td>IVA (21%):</td><td>{{orderTax}}</td></tr>"
                + "<tr class=\"total\"><td>Total:</td><td>{{orderTotal}}</td></tr>"
                + "</table>"
                + "<p>Dirección de envío: {{shippingAddress}}</p>"
                + "</body></html>";

        // Mocking static methods of Files and Paths to avoid real file system access
        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            Path fakePath = Mockito.mock(Path.class);
            mockedPaths.when(() -> Paths.get(Mockito.anyString())).thenReturn(fakePath);
            mockedFiles.when(() -> Files.readString(fakePath)).thenReturn(templateContent);

            // When
            String result = com.infinia.sports.mail.OrderMailTemplateUtil.generateOrderSummaryHtml(order);

            // Then
            assertNotNull(result);
            assertTrue(result.contains("Juan Pérez"));
            assertTrue(result.contains("ORD-12345"));
            assertTrue(result.contains("Zapatillas Running"));
            assertTrue(result.contains("159.98€"), "Debe contener el subtotal");
            assertTrue(result.contains("4.99€"), "Debe contener los gastos de envío");
            assertTrue(result.contains("34.65€"), "Debe contener el IVA");
            assertTrue(result.contains("199.62€"), "Debe contener el total");
            assertTrue(result.contains("Calle Principal 123 Apartamento 4B"));
        }
    }

    @Test
    void testGenerateOrderSummaryHtml_WithNullAddressLine2() throws IOException {
        // Given
        address.setAddressLine2(null);
        String templateContent = "<p>{{shippingAddress}}</p>";

        try (MockedStatic<Paths> mockedPaths = Mockito.mockStatic(Paths.class);
             MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {

            Path fakePath = Mockito.mock(Path.class);
            mockedPaths.when(() -> Paths.get(Mockito.anyString())).thenReturn(fakePath);
            mockedFiles.when(() -> Files.readString(fakePath)).thenReturn(templateContent);

            // When
            String result = com.infinia.sports.mail.OrderMailTemplateUtil.generateOrderSummaryHtml(order);

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
            mockedFiles.when(() -> Files.readString(Mockito.any(Path.class)))
                    .thenThrow(new IOException("File not found"));

            // When
            String result = com.infinia.sports.mail.OrderMailTemplateUtil.generateOrderSummaryHtml(order);

            // Then
            assertEquals("Gracias por tu compra. Pedido: ORD-12345", result);
        }
    }
}
