package com.infinia.sports.mapper;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Cart.CartItem;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CartMapperTest {
    @Test
    void toDTO_mapsFieldsCorrectly() {
        CartItem item = CartItem.builder()
                .id("1").productId("2").productName("Prod")
                .description("desc").quantity(3).unitPrice(new BigDecimal("10.00")).build();
        Cart cart = Cart.builder()
                .id("9").userId("8").sessionId("sid").userEmail("a@b.com")
                .items(Collections.singletonList(item))
                .subtotal(new BigDecimal("30.00"))
                .tax(new BigDecimal("3.00"))
                .total(new BigDecimal("33.00")).build();
        CartDTO dto = CartMapper.toDTO(cart);
        assertEquals(cart.getId(), dto.getId());
        assertEquals(cart.getUserId(), dto.getUserId());
        assertEquals(cart.getSessionId(), dto.getSessionId());
        assertEquals(cart.getUserEmail(), dto.getUserEmail());
        assertEquals(cart.getSubtotal(), dto.getSubtotal());
        assertEquals(cart.getTax(), dto.getTax());
        assertEquals(cart.getTotal(), dto.getTotal());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());
        CartItemDTO dtoItem = dto.getItems().get(0);
        assertEquals(item.getId(), dtoItem.getId());
        assertEquals(item.getProductId(), dtoItem.getProductId());
        assertEquals(item.getProductName(), dtoItem.getProductName());
        assertEquals(item.getDescription(), dtoItem.getDescription());
        assertEquals(item.getQuantity(), dtoItem.getQuantity());
        assertEquals(item.getUnitPrice(), dtoItem.getUnitPrice());
    }

    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(CartMapper.toDTO(null));
    }
}
