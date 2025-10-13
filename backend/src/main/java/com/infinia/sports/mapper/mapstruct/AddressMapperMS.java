package com.infinia.sports.mapper.mapstruct;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import org.mapstruct.Mapper;

/**
 * Mapper de MapStruct para Address (bidireccional)
 */
@Mapper(config = MapStructConfig.class)
public interface AddressMapperMS {
    AddressDTO toDTO(Order.Address address);
    Order.Address fromDTO(AddressDTO dto);
}
