package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper de MapStruct para Cart
 */
@Mapper(config = MapStructConfig.class)
public interface CartMapperMS {
    CartDTO toDTO(Cart cart);
    List<CartItemDTO> toItemDTOList(List<Cart.CartItem> items);
    CartItemDTO toItemDTO(Cart.CartItem item);
}
