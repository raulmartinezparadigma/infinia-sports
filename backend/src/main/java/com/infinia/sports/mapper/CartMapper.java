package com.infinia.sports.mapper;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Cart.CartItem;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilidad para mapear la entidad Cart a CartDTO para exponer datos seguros al frontend
 */
public class CartMapper {

    /**
     * Convierte un Cart a CartDTO
     */
    public static CartDTO toDTO(Cart cart) {
        if (cart == null) return null;
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .sessionId(cart.getSessionId())
                .userEmail(cart.getUserEmail())
                .items(toItemDTOList(cart.getItems()))
                .subtotal(cart.getSubtotal())
                .tax(cart.getTax())
                .total(cart.getTotal())
                .build();
    }

    /**
     * Convierte una lista de CartItem a CartItemDTO
     */
    private static List<CartItemDTO> toItemDTOList(List<CartItem> items) {
        if (items == null) return null;
        return items.stream().map(CartMapper::toItemDTO).collect(Collectors.toList());
    }

    /**
     * Convierte un CartItem a CartItemDTO
     */
    private static CartItemDTO toItemDTO(CartItem item) {
        if (item == null) return null;
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .attributes(item.getAttributes())
                .productImageUrl(item.getProductImageUrl())
                .build();
    }
}
