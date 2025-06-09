package com.infinia.sports.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para el controlador de checkout
 */
@WebMvcTest(CheckoutController.class)
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckoutService checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cart testCart;
    private Order testOrder;
    private CartItemDTO testCartItemDTO;
    private AddressDTO testAddressDTO;
    private CheckoutDTO testCheckoutDTO;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        String cartId = UUID.randomUUID().toString();
        String itemId = UUID.randomUUID().toString();
        
        // Crear item para el carrito
        Cart.CartItem cartItem = Cart.CartItem.builder()
                .id(itemId)
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .totalPrice(new BigDecimal("99.98"))
                .attributes(new HashMap<>())
                .build();
        
        // Crear carrito
        testCart = Cart.builder()
                .id(cartId)
                .userId("test-user")
                .sessionId("test-session")
                .items(new ArrayList<>(List.of(cartItem)))
                .subtotal(new BigDecimal("99.98"))
                .tax(new BigDecimal("21.00"))
                .total(new BigDecimal("120.98"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Crear DTO para item del carrito
        testCartItemDTO = CartItemDTO.builder()
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .build();
        
        // Crear DTO para dirección
        testAddressDTO = AddressDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .city("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("+34600000000")
                .build();
        
        // Crear DTO para checkout
        testCheckoutDTO = CheckoutDTO.builder()
                .cartId(cartId)
                .email("juan.perez@example.com")
                .shippingAddress(testAddressDTO)
                .sameAsBillingAddress(true)
                .shippingMethod("STANDARD")
                .paymentMethod("CREDIT_CARD")
                .build();
        
        // Crear LineItem para la orden
        Order.LineItem lineItem = Order.LineItem.builder()
                .id(itemId)
                .productId("PROD-001")
                .productName("Balón de fútbol profesional")
                .quantity(2)
                .unitPrice(new BigDecimal("49.99"))
                .totalPrice(new BigDecimal("99.98"))
                .attributes(new HashMap<>())
                .build();
                
        // Crear ShippingGroup con LineItems
        Order.ShippingGroup shippingGroup = Order.ShippingGroup.builder()
                .id("0")
                .shippingMethod("STANDARD")
                .shippingCost(new BigDecimal("0.00"))
                .lineItems(List.of(lineItem))
                .build();
        
        // Crear información de precios
        Order.PriceInfo priceInfo = Order.PriceInfo.builder()
                .subtotal(new BigDecimal("99.98"))
                .shipping(new BigDecimal("0.00"))
                .tax(new BigDecimal("21.00"))
                .discount(new BigDecimal("0.00"))
                .total(new BigDecimal("120.98"))
                .build();
        
        // Crear dirección para la orden
        Order.Address address = Order.Address.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .city("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("+34600000000")
                .build();
                
        // Crear orden
        testOrder = Order.builder()
                .id(UUID.randomUUID().toString())
                .orderId("ORD-" + System.currentTimeMillis())
                .language("es")
                .status("PENDIENTE")
                .email("juan.perez@example.com")
                .submitDate(LocalDateTime.now())
                .shippingGroups(List.of(shippingGroup))
                .shippingAddress(address)
                .billingAddress(address)
                .priceInfo(priceInfo)
                .build();
        
        // Configurar comportamiento del servicio mock
        when(checkoutService.addItemToCart(anyString(), anyString(), any(CartItemDTO.class)))
                .thenReturn(testCart);
        
        when(checkoutService.getCart(anyString(), anyString()))
                .thenReturn(testCart);
        
        when(checkoutService.removeItemFromCart(anyString(), anyString(), anyString()))
                .thenReturn(testCart);
        
        when(checkoutService.saveAddresses(anyString(), any(AddressDTO.class), any(AddressDTO.class), any(Boolean.class)))
                .thenReturn(testCart);
        
        when(checkoutService.confirmOrder(any(CheckoutDTO.class)))
                .thenReturn(testOrder);
        
        when(checkoutService.getOrder(anyString()))
                .thenReturn(testOrder);
    }

    /**
     * Prueba para añadir un producto al carrito
     */
    @Test
    void testAddItemToCart() throws Exception {
        mockMvc.perform(post("/carrito/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-ID", "test-user")
                .content(objectMapper.writeValueAsString(testCartItemDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value("PROD-001"));
    }

    /**
     * Prueba para obtener el contenido del carrito
     */
    @Test
    void testGetCart() throws Exception {
        mockMvc.perform(get("/carrito")
                .header("User-ID", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    /**
     * Prueba para eliminar un producto del carrito
     */
    @Test
    void testRemoveItemFromCart() throws Exception {
        String itemId = testCart.getItems().get(0).getId();
        
        mockMvc.perform(delete("/carrito/items/" + itemId)
                .header("User-ID", "test-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

      /**
     * Prueba para confirmar pedido
     */
    @Test
    void testConfirmOrder() throws Exception {
        mockMvc.perform(post("/checkout/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCheckoutDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.status").value("PENDIENTE"));
    }

    /**
     * Prueba para obtener información de un pedido
     */
    @Test
    void testGetOrder() throws Exception {
        mockMvc.perform(get("/pedidos/" + testOrder.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"));
    }
}
