package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para el proceso de checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {
    
    @NotBlank(message = "El ID del carrito es obligatorio")
    private String cartId;
    
    @Email(message = "El formato del email es inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @Valid
    @NotNull(message = "La dirección de envío es obligatoria")
    private AddressDTO shippingAddress;
    
    @Valid
    private AddressDTO billingAddress;
    
    private boolean sameAsBillingAddress;
    
    private String shippingMethod;
    
    private String paymentMethod;
    
    private String notes;
}
