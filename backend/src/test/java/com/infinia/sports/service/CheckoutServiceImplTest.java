package com.infinia.sports.service;

import com.infinia.sports.exception.ResourceNotFoundException;
import com.infinia.sports.mapper.OrderMapper;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.*;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.impl.CheckoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CheckoutServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.21");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(checkoutService, "shippingCost", new BigDecimal("4.99"));
    }

    @Test
    void confirmOrder_createsOrderAndDeletesCart_whenOrderIsNew() {
        // --- Arrange ---
        String cartId = "cart-123";
        String userId = "user-abc";
        
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartId(cartId);
        checkoutDTO.setEmail("test@example.com");
        checkoutDTO.setShippingAddress(new AddressDTO());

        Cart cart = new Cart();
        cart.setId(cartId);
        cart.setUserId(userId);

        Order order = new Order();
        order.setId("order-456");
        order.setOrderId(cartId);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId("order-456");

        when(orderRepository.findByOrderId(cartId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.fromCartAndCheckout(any(Cart.class), any(CheckoutDTO.class)))
                             .thenReturn(order);
            mockedOrderMapper.when(() -> OrderMapper.toDTO(any(Order.class)))
                             .thenReturn(orderDTO);

            // --- Act ---
            OrderDTO result = checkoutService.confirmOrder(checkoutDTO);

            // --- Assert ---
            assertNotNull(result);
            assertEquals(order.getId(), result.getId());
            mockedOrderMapper.verify(() -> OrderMapper.fromCartAndCheckout(cart, checkoutDTO));
            verify(orderRepository, times(1)).save(order);
            verify(cartRepository, times(1)).deleteByUserId(userId);
            mockedOrderMapper.verify(() -> OrderMapper.toDTO(order));
        }
    }

    @Test
    void confirmOrder_returnsExistingOrder_whenOrderAlreadyExists() {
        // --- Arrange ---
        String cartId = "cart-123";
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartId(cartId);

        Order existingOrder = new Order();
        existingOrder.setId("order-456");
        existingOrder.setOrderId(cartId);
        
        OrderDTO existingOrderDTO = new OrderDTO();
        existingOrderDTO.setId("order-456");

        when(orderRepository.findByOrderId(cartId)).thenReturn(Optional.of(existingOrder));

        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.toDTO(any(Order.class))).thenReturn(existingOrderDTO);

            // --- Act ---
            OrderDTO result = checkoutService.confirmOrder(checkoutDTO);

            // --- Assert ---
            assertNotNull(result);
            assertEquals(existingOrder.getId(), result.getId());
            verify(orderRepository, never()).save(any(Order.class));
            verify(cartRepository, never()).delete(any(Cart.class));
        }
    }

    @Test
    void confirmOrder_throwsResourceNotFoundException_whenCartDoesNotExist() {
        // --- Arrange ---
        String cartId = "non-existent-cart";
        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setCartId(cartId);

        when(orderRepository.findByOrderId(cartId)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        assertThrows(ResourceNotFoundException.class, () -> checkoutService.confirmOrder(checkoutDTO));
    }

    @Test
    void testAddItemToCart_Success() {
        String sessionId = "sess1";
        String userId = "user1";
        CartItemDTO itemDTO = CartItemDTO.builder().productId("prod1").productName("Producto 1").description("desc").quantity(2).unitPrice(BigDecimal.TEN).build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        CartDTO result = checkoutService.addItemToCart(sessionId, userId, itemDTO);
        assertNotNull(result);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testAddItemToCart_ProductNotFound() {
        String sessionId = "sess2";
        String userId = "user2";
        CartItemDTO itemDTO = CartItemDTO.builder().productId("prod-notfound").quantity(1).unitPrice(BigDecimal.TEN).build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        assertThrows(NullPointerException.class, () -> checkoutService.addItemToCart(sessionId, userId, itemDTO));
    }

    @Test
    void testUpdateCartItemQuantity_Success() {
        String sessionId = "sess10";
        String userId = "user10";
        String itemId = "itemW";
        Cart.CartItem item = new Cart.CartItem();
        item.setId(itemId);
        item.setProductId("prod1");
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CartDTO result = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, 5);

        assertNotNull(result);
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testRemoveItemFromCart_Success() {
        String sessionId = "sess1";
        String userId = "user1";
        String itemId = "item1";
        Cart.CartItem item = Cart.CartItem.builder().id(itemId).build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        CartDTO result = checkoutService.removeItemFromCart(sessionId, userId, itemId);
        assertNotNull(result);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testGetCart_Success() {
        String sessionId = "sess1";
        String userId = "user1";
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        CartDTO result = checkoutService.getCart(sessionId, userId);
        assertNotNull(result);
    }

    @Test
    void testClearCart_Success() {
        checkoutService.clearCart("sess1", "user1");
        verify(cartRepository).deleteByUserIdOrSessionId("user1", "sess1");
    }

    @Test
    void getCart_createsNewCart_whenNoCartExistsForUserOrSession() {
        String userId = "new-user";
        String sessionId = "new-session";

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart cartToSave = inv.getArgument(0);
            cartToSave.setId("new-cart-id"); // Ensure ID is set for the assertion
            return cartToSave;
        });

        CartDTO result = checkoutService.getCart(sessionId, userId);

        assertNotNull(result);
        assertEquals("new-cart-id", result.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItemToCart_updatesQuantity_whenItemAlreadyExists() {
        String userId = "user1";
        String sessionId = "sess1";
        String productId = "prod1";

        Cart.CartItem existingItem = Cart.CartItem.builder()
            .id("item1")
            .productId(productId)
            .quantity(1)
            .unitPrice(BigDecimal.TEN)
            .totalPrice(BigDecimal.TEN)
            .build();

        Cart cart = new Cart();
        cart.setId("cart1");
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>(List.of(existingItem)));

        CartItemDTO newItemDTO = CartItemDTO.builder()
            .productId(productId)
            .quantity(2)
            .unitPrice(BigDecimal.TEN)
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDTO result = checkoutService.addItemToCart(sessionId, userId, newItemDTO);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(3, result.getItems().get(0).getQuantity()); // 1 + 2
        assertEquals(new BigDecimal("30.00"), result.getItems().get(0).getTotalPrice());
    }

    @Test
    void updateCartItemQuantity_throwsException_whenItemNotFound() {
        String userId = "user1";
        String sessionId = "sess1";
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, 
            () -> checkoutService.updateCartItemQuantity(sessionId, userId, "non-existent-item", 2));
    }

    @Test
    void updateCartItemQuantity_removesItem_whenQuantityIsZero() {
        String userId = "user1";
        String sessionId = "sess1";
        String itemId = "item1";

        Cart.CartItem item = Cart.CartItem.builder().id(itemId).quantity(1).unitPrice(BigDecimal.TEN).build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDTO result = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, 0);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void removeItemFromCart_throwsException_whenItemNotFound() {
        String userId = "user1";
        String sessionId = "sess1";
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, 
            () -> checkoutService.removeItemFromCart(sessionId, userId, "non-existent-item"));
    }

    @Test
    void linkCartToUser_success() {
        String cartId = "cart-to-link";
        String userId = "user-to-link";
        String userEmail = "link@example.com";
        Cart cart = new Cart();
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartDTO result = checkoutService.linkCartToUser(cartId, userId, userEmail);

        assertNotNull(result);
        assertEquals(userId, cart.getUserId());
        assertEquals(userEmail, cart.getUserEmail());
        verify(cartRepository).save(cart);
    }

    @Test
    void linkCartToUser_throwsException_whenCartNotFound() {
        when(cartRepository.findById("non-existent-cart")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> checkoutService.linkCartToUser("non-existent-cart", "user", "email"));
    }

    @Test
    void saveAddresses_success() {
        String cartId = "cart-with-address";
        Cart cart = new Cart();
        cart.setId(cartId);
        AddressDTO address = new AddressDTO();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        checkoutService.saveAddresses(cartId, address, address, true);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void saveAddresses_throwsException_whenCartNotFound() {
        when(cartRepository.findById("non-existent-cart")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> checkoutService.saveAddresses("non-existent-cart", new AddressDTO(), new AddressDTO(), false));
    }

    @Test
    void shouldCalculateTotalsCorrectly() {
        // Arrange
        String userId = "user-totals";
        String sessionId = "session-totals";
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());

        Cart.CartItem item1 = Cart.CartItem.builder()
            .id("item-1")
            .productId("prod-1")
            .quantity(2)
            .unitPrice(new BigDecimal("10.00")) // 2 * 10.00 = 20.00
            .totalPrice(new BigDecimal("20.00"))
            .build();
        cart.getItems().add(item1);

        Cart.CartItem item2 = Cart.CartItem.builder()
            .id("item-2")
            .productId("prod-2")
            .quantity(1)
            .unitPrice(new BigDecimal("5.50")) // 1 * 5.50 = 5.50
            .totalPrice(new BigDecimal("5.50"))
            .build();
        cart.getItems().add(item2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: Añadir un item para forzar el recálculo de totales
        CartItemDTO newItemDTO = CartItemDTO.builder().productId("prod-new").quantity(1).unitPrice(BigDecimal.ZERO).build();
        CartDTO result = checkoutService.addItemToCart(sessionId, userId, newItemDTO); // La lógica de cálculo está en addItemToCart

        // Assert
        BigDecimal expectedSubtotal = new BigDecimal("25.50"); // 20.00 + 5.50
        BigDecimal expectedTax = expectedSubtotal.multiply(DEFAULT_TAX_RATE).setScale(2, RoundingMode.HALF_UP); // 25.50 * 0.21 = 5.36
        BigDecimal expectedShippingCost = new BigDecimal("4.99");
        BigDecimal expectedTotal = expectedSubtotal.add(expectedTax).add(expectedShippingCost); // 25.50 + 5.36 + 4.99 = 35.85

        assertEquals(expectedSubtotal, result.getSubtotal());
        assertEquals(expectedTax, result.getTax());
        assertEquals(expectedShippingCost, result.getShippingCost());
        assertEquals(expectedTotal, result.getTotal());
    }
}
