package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Order.LineItem;
import com.infinia.sports.model.Order.ShippingGroup;
import com.infinia.sports.model.Order.PriceInfo;
import com.infinia.sports.model.Order.TaxInfo;
import com.infinia.sports.model.Order.Address;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Cart.CartItem;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.repository.jpa.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
    @Mock
    private ProductRepository productRepository;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        OrderMapper.setProductRepository(productRepository);
    }
    
    @Test
    void toDTO_mapsFieldsCorrectly() {
        Map<String, BigDecimal> breakdown = new HashMap<>();
        breakdown.put("IVA", new BigDecimal("21.00"));
        LineItem item = LineItem.builder()
                .id("1").productId("2").productName("Prod").quantity(1)
                .unitPrice(new BigDecimal("10.00")).totalPrice(new BigDecimal("10.00"))
                .productImageUrl("img.png").build();
        ShippingGroup group = ShippingGroup.builder()
                .id("100").shippingMethod("Express").shippingCost(new BigDecimal("5.00"))
                .lineItems(Collections.singletonList(item)).build();
        PriceInfo priceInfo = PriceInfo.builder()
                .subtotal(new BigDecimal("10.00")).tax(new BigDecimal("2.00"))
                .discount(new BigDecimal("1.00")).total(new BigDecimal("11.00")).build();
        TaxInfo taxInfo = TaxInfo.builder()
                .taxRate(new BigDecimal("0.21")).taxRegion("ES")
                .taxBreakdown(breakdown).build();
        Address dummyAddress = Address.builder()
            .firstName("Juan")
            .lastName("Pérez")
            .addressLine1("Calle Falsa 123")
            .addressLine2("")
            .city("Springfield")
            .state("Madrid")
            .postalCode("12345")
            .country("España")
            .phoneNumber("600123123")
            .build();
        Order order = Order.builder()
                .id("1").orderId("ORD123").userId("2").status("PAID").email("a@b.com")
                .language("es").submitDate(LocalDateTime.of(2024, 1, 1, 0, 0))
                .shippingGroups(Collections.singletonList(group))
                .shippingAddress(dummyAddress).billingAddress(dummyAddress)
                .priceInfo(priceInfo).taxInfo(taxInfo).build();
        OrderDTO dto = OrderMapper.toDTO(order);
        if (dto == null) {
            System.out.println("OrderMapper.toDTO devolvió null. Order de entrada: " + order);
        }
        assertNotNull(dto, "OrderMapper.toDTO devolvió null");
        assertEquals(order.getId(), dto.getId());
        assertEquals(order.getOrderId(), dto.getOrderId());
        assertEquals(order.getUserId(), dto.getUserId());
        assertEquals(order.getStatus(), dto.getStatus());
        assertEquals(order.getEmail(), dto.getEmail());
        assertEquals(order.getLanguage(), dto.getLanguage());
        assertEquals(order.getSubmitDate(), dto.getSubmitDate());
        assertNotNull(dto.getShippingGroups());
        assertEquals(1, dto.getShippingGroups().size());
        assertNotNull(dto.getPriceInfo());
        assertNotNull(dto.getTaxInfo());
    }

    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(OrderMapper.toDTO(null));
    }
    
    @Test
    void fromCart_mapsFieldsCorrectly() {
        // Crear datos de prueba
        Cart cart = new Cart();
        cart.setId("cart123");
        cart.setSubtotal(new BigDecimal("100.00"));
        cart.setTax(new BigDecimal("21.00"));
        cart.setTotal(new BigDecimal("121.00"));
        
        CartItem cartItem = new CartItem();
        cartItem.setId("item1");
        cartItem.setProductId("prod1");
        cartItem.setProductName("Producto de prueba");
        cartItem.setQuantity(2);
        cartItem.setUnitPrice(new BigDecimal("50.00"));
        cartItem.setTotalPrice(new BigDecimal("100.00"));
        cartItem.setProductImageUrl("http://example.com/image.jpg");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("color", "rojo");
        attributes.put("talla", "M");
        cartItem.setAttributes(attributes);
        
        cart.setItems(Collections.singletonList(cartItem));
        
        AddressDTO shippingAddress = AddressDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .city("Madrid")
                .state("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("600111222")
                .email("juan@example.com")
                .build();
        
        AddressDTO billingAddress = AddressDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Secundaria 456")
                .city("Barcelona")
                .state("Barcelona")
                .postalCode("08001")
                .country("España")
                .phoneNumber("600333444")
                .email("juan@example.com")
                .build();
        
        // Ejecutar el método a probar
        Order order = OrderMapper.fromCart(cart, shippingAddress, billingAddress);
        
        // Verificar resultados
        assertNotNull(order);
        assertEquals(cart.getId(), order.getId());
        assertEquals(cart.getId(), order.getOrderId());
        assertEquals("ES", order.getLanguage());
        assertEquals("pending", order.getStatus());
        assertEquals(shippingAddress.getEmail(), order.getEmail());
        
        // Verificar ShippingGroup
        assertNotNull(order.getShippingGroups());
        assertEquals(1, order.getShippingGroups().size());
        ShippingGroup group = order.getShippingGroups().get(0);
        assertEquals("1", group.getId());
        assertEquals("Infinia Sports", group.getShippingMethod());
        
        // Verificar LineItems
        assertNotNull(group.getLineItems());
        assertEquals(1, group.getLineItems().size());
        LineItem lineItem = group.getLineItems().get(0);
        assertEquals(cartItem.getId(), lineItem.getId());
        assertEquals(cartItem.getProductId(), lineItem.getProductId());
        assertEquals(cartItem.getProductName(), lineItem.getProductName());
        assertEquals(cartItem.getQuantity(), lineItem.getQuantity());
        assertEquals(cartItem.getUnitPrice(), lineItem.getUnitPrice());
        assertEquals(cartItem.getTotalPrice(), lineItem.getTotalPrice());
        assertEquals(cartItem.getProductImageUrl(), lineItem.getProductImageUrl());
        assertEquals(cartItem.getAttributes(), lineItem.getAttributes());
        
        // Verificar direcciones
        assertNotNull(order.getShippingAddress());
        assertEquals(shippingAddress.getFirstName(), order.getShippingAddress().getFirstName());
        assertEquals(shippingAddress.getAddressLine1(), order.getShippingAddress().getAddressLine1());
        
        assertNotNull(order.getBillingAddress());
        assertEquals(billingAddress.getFirstName(), order.getBillingAddress().getFirstName());
        assertEquals(billingAddress.getAddressLine1(), order.getBillingAddress().getAddressLine1());
        
        // Verificar PriceInfo
        assertNotNull(order.getPriceInfo());
        assertEquals(cart.getSubtotal(), order.getPriceInfo().getSubtotal());
        assertEquals(cart.getTax(), order.getPriceInfo().getTax());
        assertEquals(cart.getTotal(), order.getPriceInfo().getTotal());
        assertEquals(BigDecimal.ZERO, order.getPriceInfo().getDiscount());
    }
    
    @Test
    void fromCartAndCheckout_mapsFieldsCorrectly() {
        // Crear datos de prueba
        Cart cart = new Cart();
        cart.setId("cart456");
        cart.setSubtotal(new BigDecimal("200.00"));
        cart.setTax(new BigDecimal("42.00"));
        cart.setTotal(new BigDecimal("242.00"));
        
        CartItem cartItem = new CartItem();
        cartItem.setId("item2");
        cartItem.setProductId("prod2");
        cartItem.setProductName("Otro producto");
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(new BigDecimal("200.00"));
        cartItem.setTotalPrice(new BigDecimal("200.00"));
        cartItem.setProductImageUrl("http://example.com/image2.jpg");
        
        cart.setItems(Collections.singletonList(cartItem));
        
        AddressDTO shippingAddress = AddressDTO.builder()
                .firstName("Ana")
                .lastName("García")
                .addressLine1("Avenida Principal 789")
                .city("Valencia")
                .state("Valencia")
                .postalCode("46001")
                .country("España")
                .phoneNumber("600555666")
                .email("ana@example.com")
                .build();
        
        AddressDTO billingAddress = AddressDTO.builder()
                .firstName("Ana")
                .lastName("García")
                .addressLine1("Avenida Principal 789")
                .city("Valencia")
                .state("Valencia")
                .postalCode("46001")
                .country("España")
                .phoneNumber("600555666")
                .email("ana@example.com")
                .build();
        
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartId(cart.getId());
        checkoutDTO.setShippingAddress(shippingAddress);
        checkoutDTO.setBillingAddress(billingAddress);
        checkoutDTO.setShippingMethod("Express 24h");
        
        // Ejecutar el método a probar
        Order order = OrderMapper.fromCartAndCheckout(cart, checkoutDTO);
        
        // Verificar resultados
        assertNotNull(order);
        assertEquals(cart.getId(), order.getId());
        assertEquals(cart.getId(), order.getOrderId());
        
        // Verificar que se ha aplicado el método de envío del CheckoutDTO
        assertNotNull(order.getShippingGroups());
        assertEquals(1, order.getShippingGroups().size());
        assertEquals("Express 24h", order.getShippingGroups().get(0).getShippingMethod());
    }
    
    @Test
    void fromCartAndCheckout_mapsFieldsAndCalculatesPricesCorrectly() {
        // --- Arrange ---
        // Crear datos de prueba para el carrito
        Cart cart = new Cart();
        cart.setId("cart-test-123");
        cart.setUserId("user-test-456");
        cart.setSubtotal(new BigDecimal("150.75"));
        cart.setTax(new BigDecimal("31.66"));

        CartItem cartItem = new CartItem();
        cartItem.setId("item-1");
        cartItem.setProductId("prod-1");
        cartItem.setProductName("Zapatillas de Running");
        cartItem.setQuantity(1);
        cartItem.setUnitPrice(new BigDecimal("150.75"));
        cartItem.setTotalPrice(new BigDecimal("150.75"));
        cart.setItems(Collections.singletonList(cartItem));

        // Crear datos de prueba para el DTO de checkout
        AddressDTO shippingAddress = AddressDTO.builder()
                .firstName("Ana").lastName("Gomez").email("ana.gomez@example.com").build();

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartId(cart.getId());
        checkoutDTO.setEmail("ana.gomez@example.com");
        checkoutDTO.setShippingAddress(shippingAddress);
        checkoutDTO.setShippingMethod("Envío Estándar");

        // --- Act ---
        // Ejecutar el método a probar
        Order order = OrderMapper.fromCartAndCheckout(cart, checkoutDTO);

        // --- Assert ---
        // Verificar los campos básicos del pedido
        assertNotNull(order);
        assertEquals(cart.getId(), order.getOrderId());
        assertEquals(cart.getUserId(), order.getUserId());
        assertEquals(checkoutDTO.getEmail(), order.getEmail());
        assertEquals("PENDING_PAYMENT", order.getStatus());
        assertNotNull(order.getSubmitDate());

        // Verificar la dirección de envío
        assertNotNull(order.getShippingAddress());
        assertEquals(shippingAddress.getFirstName(), order.getShippingAddress().getFirstName());

        // Verificar el grupo de envío y los items
        assertNotNull(order.getShippingGroups());
        assertEquals(1, order.getShippingGroups().size());
        ShippingGroup shippingGroup = order.getShippingGroups().get(0);
        assertEquals(checkoutDTO.getShippingMethod(), shippingGroup.getShippingMethod());
        assertEquals(1, shippingGroup.getLineItems().size());
        assertEquals(cartItem.getProductId(), shippingGroup.getLineItems().get(0).getProductId());

        // Verificar los cálculos de precios (la parte más importante)
        assertNotNull(order.getPriceInfo());
        PriceInfo priceInfo = order.getPriceInfo();
        
        // Comprobar que el coste de envío es CERO
        assertEquals(BigDecimal.ZERO, shippingGroup.getShippingCost());

        // Comprobar que los valores se han escalado a 2 decimales
        assertEquals(new BigDecimal("150.75"), priceInfo.getSubtotal());
        assertEquals(new BigDecimal("31.66"), priceInfo.getTax());
        assertEquals(BigDecimal.ZERO, priceInfo.getDiscount());

        // Comprobar el cálculo del total
        BigDecimal expectedTotal = new BigDecimal("150.75").add(new BigDecimal("31.66")).add(BigDecimal.ZERO);
        assertEquals(expectedTotal.setScale(2, java.math.RoundingMode.HALF_UP), priceInfo.getTotal());
    }
}
