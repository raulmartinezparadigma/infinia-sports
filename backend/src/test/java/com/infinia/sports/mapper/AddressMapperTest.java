package com.infinia.sports.mapper;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressMapperTest {

    @Test
    void toDTO_mapsFieldsCorrectly() {
        // Crear Address de prueba
        Order.Address address = Order.Address.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .addressLine1("Calle Principal 123")
                .addressLine2("Piso 4B")
                .city("Madrid")
                .state("Madrid")
                .postalCode("28001")
                .country("España")
                .phoneNumber("600111222")
                .build();
        
        // Ejecutar el método a probar
        AddressDTO dto = AddressMapper.toDTO(address);
        
        // Verificar resultados
        assertNotNull(dto);
        assertEquals(address.getFirstName(), dto.getFirstName());
        assertEquals(address.getLastName(), dto.getLastName());
        assertEquals(address.getAddressLine1(), dto.getAddressLine1());
        assertEquals(address.getAddressLine2(), dto.getAddressLine2());
        assertEquals(address.getCity(), dto.getCity());
        assertEquals(address.getState(), dto.getState());
        assertEquals(address.getPostalCode(), dto.getPostalCode());
        assertEquals(address.getCountry(), dto.getCountry());
        assertEquals(address.getPhoneNumber(), dto.getPhoneNumber());
    }
    
    @Test
    void toDTO_nullInput_returnsNull() {
        assertNull(AddressMapper.toDTO(null));
    }
    
    @Test
    void fromDTO_mapsFieldsCorrectly() {
        // Crear AddressDTO de prueba
        AddressDTO dto = AddressDTO.builder()
                .firstName("Ana")
                .lastName("García")
                .addressLine1("Avenida Principal 789")
                .addressLine2("Bloque 2, 3º")
                .city("Valencia")
                .state("Valencia")
                .postalCode("46001")
                .country("España")
                .phoneNumber("600555666")
                .email("ana@example.com")
                .build();
        
        // Ejecutar el método a probar
        Order.Address address = AddressMapper.fromDTO(dto);
        
        // Verificar resultados
        assertNotNull(address);
        assertEquals(dto.getFirstName(), address.getFirstName());
        assertEquals(dto.getLastName(), address.getLastName());
        assertEquals(dto.getAddressLine1(), address.getAddressLine1());
        assertEquals(dto.getAddressLine2(), address.getAddressLine2());
        assertEquals(dto.getCity(), address.getCity());
        assertEquals(dto.getState(), address.getState());
        assertEquals(dto.getPostalCode(), address.getPostalCode());
        assertEquals(dto.getCountry(), address.getCountry());
        assertEquals(dto.getPhoneNumber(), address.getPhoneNumber());
    }
    
    @Test
    void fromDTO_nullInput_returnsNull() {
        assertNull(AddressMapper.fromDTO(null));
    }
}
