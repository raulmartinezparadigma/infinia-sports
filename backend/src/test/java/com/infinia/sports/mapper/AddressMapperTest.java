package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {
    @Test
    void toDTO_mapsFieldsCorrectly() {
        Order.Address address = Order.Address.builder()
                .firstName("John").lastName("Doe").addressLine1("Street 1")
                .addressLine2("Apt 2").city("City").state("State")
                .postalCode("12345").country("Country").phoneNumber("555-1234").build();
        AddressDTO dto = AddressMapper.toDTO(address);
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("Street 1", dto.getAddressLine1());
        assertEquals("Apt 2", dto.getAddressLine2());
        assertEquals("City", dto.getCity());
        assertEquals("State", dto.getState());
        assertEquals("12345", dto.getPostalCode());
        assertEquals("Country", dto.getCountry());
        assertEquals("555-1234", dto.getPhoneNumber());
    }

    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(AddressMapper.toDTO(null));
    }
}
