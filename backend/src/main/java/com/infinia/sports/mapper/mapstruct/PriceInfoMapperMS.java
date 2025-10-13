package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.PriceInfoDTO;
import org.mapstruct.Mapper;

/**
 * Mapper de MapStruct para PriceInfo
 */
@Mapper(config = MapStructConfig.class)
public interface PriceInfoMapperMS {
    PriceInfoDTO toDTO(Order.PriceInfo priceInfo);
}
