package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;

public class AddressMapper {
    public static AddressDTO toDTO(Order.Address address) {
        if (address == null) return null;
        return AddressDTO.builder()
                .firstName(address.getFirstName())
                .lastName(address.getLastName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phoneNumber(address.getPhoneNumber())
                .build();
    }
    
    /**
     * Convierte un AddressDTO a Order.Address
     */
    public static Order.Address fromDTO(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        
        return Order.Address.builder()
                .firstName(addressDTO.getFirstName())
                .lastName(addressDTO.getLastName())
                .addressLine1(addressDTO.getAddressLine1())
                .addressLine2(addressDTO.getAddressLine2())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .postalCode(addressDTO.getPostalCode())
                .country(addressDTO.getCountry())
                .phoneNumber(addressDTO.getPhoneNumber())
                .build();
    }
}
