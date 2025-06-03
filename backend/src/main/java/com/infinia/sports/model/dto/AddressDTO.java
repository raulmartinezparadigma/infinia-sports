package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para la gestión de direcciones de envío y facturación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String addressLine1;
    
    private String addressLine2;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;
    
    private String state;
    
    @NotBlank(message = "El código postal es obligatorio")
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Formato de código postal inválido")
    private String postalCode;
    
    @NotBlank(message = "El país es obligatorio")
    private String country;
    
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$", message = "Formato de teléfono inválido")
    private String phoneNumber;
}
