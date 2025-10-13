package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.TaxInfoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper de MapStruct para TaxInfo
 */
@Mapper(config = MapStructConfig.class)
public interface TaxInfoMapperMS {
    TaxInfoDTO toDTO(Order.TaxInfo taxInfo);
}
