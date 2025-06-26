package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Order.LineItem;
import com.infinia.sports.model.Order.ShippingGroup;
import com.infinia.sports.model.Order.PriceInfo;
import com.infinia.sports.model.Order.TaxInfo;
import com.infinia.sports.model.Order.Address;
import com.infinia.sports.model.dto.OrderDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {
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
}
