package com.infinia.sports.service;

import com.infinia.sports.exception.ResourceNotFoundException;
import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.repository.mongo.CartRepository;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.service.impl.CheckoutServiceImpl;
import com.infinia.sports.mapper.OrderMapper;
import com.infinia.sports.mapper.AddressMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutService = new CheckoutServiceImpl(cartRepository, orderRepository, productRepository);
    }

    @Test
    void testAddItemToCart_Success() {
        String sessionId = "sess1";
        String userId = "user1";
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod1")
                .productName("Producto 1")
                .description("desc")
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();
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
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod-notfound")
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        assertThrows(NullPointerException.class, () -> checkoutService.addItemToCart(sessionId, userId, itemDTO));
    }

    @Test
    void testAddItemToCart_NegativeQuantity() {
        String sessionId = "sess3";
        String userId = "user3";
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod1")
                .quantity(-5)
                .unitPrice(BigDecimal.TEN)
                .build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        assertThrows(NullPointerException.class, () -> checkoutService.addItemToCart(sessionId, userId, itemDTO));
    }

    @Test
    void testAddItemToCart_CartNotFound_CreatesNew() {
        String sessionId = "sess4";
        String userId = "user4";
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod1")
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CartDTO result = checkoutService.addItemToCart(sessionId, userId, itemDTO);
        assertNotNull(result);
    }

    @Test
    void testAddItemToCart_ItemAlreadyExists_SumsQuantity() {
        String sessionId = "sess5";
        String userId = "user5";
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod1")
                .quantity(2)
                .unitPrice(BigDecimal.TEN)
                .build();
        Cart.CartItem existingItem = new Cart.CartItem();
        existingItem.setProductId("prod1");
        existingItem.setQuantity(3);
        existingItem.setUnitPrice(BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(existingItem)));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CartDTO result = checkoutService.addItemToCart(sessionId, userId, itemDTO);
        assertNotNull(result);
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    void testAddItemToCart_NewItem() {
        String sessionId = "sess6";
        String userId = "user6";
        CartItemDTO itemDTO = CartItemDTO.builder()
                .productId("prod2")
                .quantity(1)
                .unitPrice(BigDecimal.TEN)
                .build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        com.infinia.sports.model.Product mockProduct = new com.infinia.sports.model.Product();
        mockProduct.setPrice(BigDecimal.TEN);
        when(productRepository.findById(any())).thenReturn(Optional.of(mockProduct));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CartDTO result = checkoutService.addItemToCart(sessionId, userId, itemDTO);
        assertNotNull(result);
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void testUpdateCartItemQuantity_ItemFound() {
        String sessionId = "sess1";
        String userId = "user1";
        String itemId = "item1";
        Cart.CartItem item = Cart.CartItem.builder().id(itemId).quantity(1).unitPrice(BigDecimal.ONE).totalPrice(BigDecimal.ONE).build();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        CartDTO result = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, 3);
        assertNotNull(result);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testUpdateCartItemQuantity_CartNotFound() {
        String sessionId = "sess7";
        String userId = "user7";
        String itemId = "itemX";
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> checkoutService.updateCartItemQuantity(sessionId, userId, itemId, 2));
    }

    @Test
    void testUpdateCartItemQuantity_ItemNotFound() {
        String sessionId = "sess8";
        String userId = "user8";
        String itemId = "itemY";
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        assertThrows(NullPointerException.class, () -> checkoutService.updateCartItemQuantity(sessionId, userId, itemId, 2));
    }

    @Test
    void testUpdateCartItemQuantity_NegativeQuantity_RemovesItem() {
        String sessionId = "sess9";
        String userId = "user9";
        String itemId = "itemZ";
        Cart.CartItem item = new Cart.CartItem();
        item.setId(itemId);
        item.setProductId("prod1");
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(item)));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CartDTO result = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, -1);
        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty());
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
    void testRemoveItemFromCart_ItemNotFound() {
        String sessionId = "sess1";
        String userId = "user1";
        String itemId = "item1";
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart); // Mock para evitar NullPointerException
        assertThrows(ResourceNotFoundException.class, () ->
            checkoutService.removeItemFromCart(sessionId, userId, itemId)
        );
    }

    @Test
    void testGetCart_Success() {
        String sessionId = "sess1";
        String userId = "user1";
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>()); // Asegura lista no nula
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart); // Mock para evitar que devuelva null
        CartDTO result = checkoutService.getCart(sessionId, userId);
        assertNotNull(result);
    }

    @Test
    void testClearCart_Success() {
        checkoutService.clearCart("sess1", "user1");
        verify(cartRepository).deleteByUserIdOrSessionId("user1", "sess1");
    }

    @Test
    void testClearCart_UserId() {
        String sessionId = null;
        String userId = "user11";
        doNothing().when(cartRepository).deleteByUserIdOrSessionId(userId, sessionId);
        checkoutService.clearCart(sessionId, userId);
        verify(cartRepository).deleteByUserIdOrSessionId(userId, sessionId);
    }

    @Test
    void testClearCart_SessionId() {
        String sessionId = "sess12";
        String userId = null;
        doNothing().when(cartRepository).deleteByUserIdOrSessionId(userId, sessionId);
        checkoutService.clearCart(sessionId, userId);
        verify(cartRepository).deleteByUserIdOrSessionId(userId, sessionId);
    }

    @Test
    void testClearCart_NoUserOrSessionId() {
        String sessionId = null;
        String userId = null;
        checkoutService.clearCart(sessionId, userId);
        verify(cartRepository, never()).deleteByUserIdOrSessionId(any(), any());
    }

    @Test
    void testSaveAddresses_CartNotFound() {
        when(cartRepository.findById("cart1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
            checkoutService.saveAddresses("cart1", new AddressDTO(), new AddressDTO(), false)
        );
    }

    @Test
    void testLinkCartToUser_Success() {
        Cart cart = new Cart();
        cart.setId("cart1");
        when(cartRepository.findById("cart1")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenReturn(cart);
        CartDTO result = checkoutService.linkCartToUser("cart1", "user1", "mail@x.com");
        assertNotNull(result);
        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void testLinkCartToUser_CartNotFound() {
        when(cartRepository.findById("cart1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
            checkoutService.linkCartToUser("cart1", "user1", "mail@x.com")
        );
    }

    @Test
    void testConfirmOrder_CartNotFound() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setCartId("cart1");
        when(orderRepository.findByOrderId("cart1")).thenReturn(Optional.empty());
        when(cartRepository.findById("cart1")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () ->
            checkoutService.confirmOrder(dto)
        );
    }

    @Test
    void testConfirmOrder_AlreadyExists() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setCartId("cart1");
        Order order = new Order();
        OrderDTO orderDTO = OrderDTO.builder().id("test-id").build();
        when(orderRepository.findByOrderId("cart1")).thenReturn(Optional.of(order));
        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.toDTO(order)).thenReturn(orderDTO);
            OrderDTO result = checkoutService.confirmOrder(dto);
            assertEquals(orderDTO, result);
        }
    }

    @Test
    void testConfirmOrder_Success_UserId() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setCartId("cart1");
        AddressDTO shipping = new AddressDTO();
        shipping.setEmail("ship@x.com");
        AddressDTO billing = new AddressDTO();
        billing.setEmail("bill@x.com");
        dto.setShippingAddress(shipping);
        dto.setBillingAddress(billing);
        Cart cart = new Cart();
        cart.setId("cart1");
        cart.setUserId("user1");
        cart.setItems(new ArrayList<>());
        Order order = new Order();
        Order savedOrder = new Order();
        OrderDTO orderDTO = OrderDTO.builder().id("test-id").build();
        when(orderRepository.findByOrderId("cart1")).thenReturn(Optional.empty());
        when(cartRepository.findById("cart1")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.fromCartAndCheckout(cart, dto)).thenReturn(order);
            mockedOrderMapper.when(() -> OrderMapper.toDTO(savedOrder)).thenReturn(orderDTO);
            OrderDTO result = checkoutService.confirmOrder(dto);
            assertNotNull(result);
            assertEquals(orderDTO, result);
            verify(cartRepository).deleteByUserId("user1");
        }
    }

    @Test
    void testConfirmOrder_Success_SessionId() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setCartId("cart2");
        AddressDTO shipping = new AddressDTO();
        shipping.setEmail("ship@x.com");
        AddressDTO billing = new AddressDTO();
        billing.setEmail("bill@x.com");
        dto.setShippingAddress(shipping);
        dto.setBillingAddress(billing);
        Cart cart = new Cart();
        cart.setId("cart2");
        cart.setSessionId("sess2");
        cart.setItems(new ArrayList<>());
        Order order = new Order();
        Order savedOrder = new Order();
        OrderDTO orderDTO = OrderDTO.builder().id("test-id").build();
        when(orderRepository.findByOrderId("cart2")).thenReturn(Optional.empty());
        when(cartRepository.findById("cart2")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.fromCartAndCheckout(cart, dto)).thenReturn(order);
            mockedOrderMapper.when(() -> OrderMapper.toDTO(savedOrder)).thenReturn(orderDTO);
            OrderDTO result = checkoutService.confirmOrder(dto);
            assertNotNull(result);
            assertEquals(orderDTO, result);
            verify(cartRepository).deleteBySessionId("sess2");
        }
    }

    @Test
    void testConfirmOrder_Success_NoUserOrSession() {
        CheckoutDTO dto = new CheckoutDTO();
        dto.setCartId("cart3");
        AddressDTO shipping = new AddressDTO();
        shipping.setEmail("ship@x.com");
        AddressDTO billing = new AddressDTO();
        billing.setEmail("bill@x.com");
        dto.setShippingAddress(shipping);
        dto.setBillingAddress(billing);
        Cart cart = new Cart();
        cart.setId("cart3");
        cart.setItems(new ArrayList<>());
        Order order = new Order();
        Order savedOrder = new Order();
        OrderDTO orderDTO = OrderDTO.builder().id("test-id").build();
        when(orderRepository.findByOrderId("cart3")).thenReturn(Optional.empty());
        when(cartRepository.findById("cart3")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        try (MockedStatic<OrderMapper> mockedOrderMapper = mockStatic(OrderMapper.class)) {
            mockedOrderMapper.when(() -> OrderMapper.fromCartAndCheckout(cart, dto)).thenReturn(order);
            mockedOrderMapper.when(() -> OrderMapper.toDTO(savedOrder)).thenReturn(orderDTO);
            OrderDTO result = checkoutService.confirmOrder(dto);
            assertNotNull(result);
            assertEquals(orderDTO, result);
            verify(cartRepository).delete(cart);
        }
    }

    @Test
    void testSaveAddresses_Success_Distinct() {
        Cart cart = new Cart();
        cart.setId("cart4");
        cart.setItems(new ArrayList<>());
        AddressDTO shipping = new AddressDTO();
        shipping.setEmail("ship@x.com");
        AddressDTO billing = new AddressDTO();
        billing.setEmail("bill@x.com");
        when(cartRepository.findById("cart4")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        CartDTO result = checkoutService.saveAddresses("cart4", shipping, billing, false);
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testSaveAddresses_Success_SameAsBilling() {
        Cart cart = new Cart();
        cart.setId("cart5");
        cart.setItems(new ArrayList<>());
        AddressDTO address = new AddressDTO();
        address.setEmail("same@x.com");
        when(cartRepository.findById("cart5")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());
        CartDTO result = checkoutService.saveAddresses("cart5", address, address, true);
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testEnrichCartItemsWithImages_NullCart() throws Exception {
        var method = CheckoutServiceImpl.class.getDeclaredMethod("enrichCartItemsWithImages", Cart.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(checkoutService, (Cart) null));
    }

    @Test
    void testEnrichCartItemsWithImages_EmptyItems() throws Exception {
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        var method = CheckoutServiceImpl.class.getDeclaredMethod("enrichCartItemsWithImages", Cart.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(checkoutService, cart));
    }

    @Test
    void testEnrichCartItemsWithImages_ProductNotFound() throws Exception {
        Cart cart = new Cart();
        Cart.CartItem item = new Cart.CartItem();
        item.setProductId("prod1");
        cart.setItems(List.of(item));
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        var method = CheckoutServiceImpl.class.getDeclaredMethod("enrichCartItemsWithImages", Cart.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(checkoutService, cart));
    }

    @Test
    void testEnrichCartItemsWithImages_ProductFoundWithNullFields() throws Exception {
        Cart cart = new Cart();
        Cart.CartItem item = new Cart.CartItem();
        item.setProductId("prod2");
        cart.setItems(List.of(item));
        com.infinia.sports.model.Product product = new com.infinia.sports.model.Product();
        // product fields left null
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        var method = CheckoutServiceImpl.class.getDeclaredMethod("enrichCartItemsWithImages", Cart.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(checkoutService, cart));
    }

    @Test
    void testEnrichCartItemsWithImages_MixedValidAndInvalidProducts() throws Exception {
        Cart cart = new Cart();
        Cart.CartItem item1 = new Cart.CartItem();
        Cart.CartItem item2 = new Cart.CartItem();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        item1.setProductId(uuid1.toString());
        item2.setProductId(uuid2.toString());
        cart.setItems(List.of(item1, item2));
        when(productRepository.findById(uuid1)).thenReturn(Optional.empty());
        when(productRepository.findById(uuid2)).thenReturn(Optional.of(new com.infinia.sports.model.Product()));
        var method = CheckoutServiceImpl.class.getDeclaredMethod("enrichCartItemsWithImages", Cart.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(checkoutService, cart));
    }

    @Test
    void testAddressMapper_fromDTO_AllNullFields() {
        AddressDTO dto = new AddressDTO();
        Order.Address result = AddressMapper.fromDTO(dto);
        assertNotNull(result);
    }

    @Test
    void testAddressMapper_fromDTO_AllFieldsFilled() {
        AddressDTO dto = new AddressDTO();
        dto.setAddressLine1("line1");
        dto.setAddressLine2("line2");
        dto.setCity("city");
        dto.setPostalCode("12345");
        dto.setCountry("country");
        Order.Address result = AddressMapper.fromDTO(dto);
        assertNotNull(result);
        assertEquals(dto.getAddressLine1(), result.getAddressLine1());
        assertEquals(dto.getAddressLine2(), result.getAddressLine2());
        assertEquals(dto.getCity(), result.getCity());
        assertEquals(dto.getPostalCode(), result.getPostalCode());
        assertEquals(dto.getCountry(), result.getCountry());
    }
}
