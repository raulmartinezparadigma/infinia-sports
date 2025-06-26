package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.Order.TaxInfo;
import com.infinia.sports.model.dto.TaxInfoDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TaxInfoMapperTest {
    @Test
    void toDTO_mapsFieldsCorrectly() {
        Map<String, BigDecimal> breakdown = new HashMap<>();
        breakdown.put("IVA", new BigDecimal("21.00"));
        TaxInfo taxInfo = TaxInfo.builder()
                .taxRate(new BigDecimal("0.21"))
                .taxRegion("ES")
                .taxBreakdown(breakdown)
                .build();
        TaxInfoDTO dto = TaxInfoMapper.toDTO(taxInfo);
        assertEquals(taxInfo.getTaxRate(), dto.getTaxRate());
        assertEquals(taxInfo.getTaxRegion(), dto.getTaxRegion());
        assertEquals(taxInfo.getTaxBreakdown(), dto.getTaxBreakdown());
    }
    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(TaxInfoMapper.toDTO(null));
    }
}
