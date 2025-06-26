package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Order.PriceInfo;
import com.infinia.sports.model.dto.PriceInfoDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PriceInfoMapperTest {
    @Test
    void toDTO_mapsFieldsCorrectly() {
        PriceInfo priceInfo = PriceInfo.builder()
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("21.00"))
                .discount(new BigDecimal("10.00"))
                .total(new BigDecimal("111.00"))
                .build();
        PriceInfoDTO dto = PriceInfoMapper.toDTO(priceInfo);
        assertEquals(priceInfo.getSubtotal(), dto.getSubtotal());
        assertEquals(priceInfo.getTax(), dto.getTax());
        assertEquals(priceInfo.getDiscount(), dto.getDiscount());
        assertEquals(priceInfo.getTotal(), dto.getTotal());
    }
    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(PriceInfoMapper.toDTO(null));
    }
}
