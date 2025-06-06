package com.infinia.sports.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para la petición de pago Bizum
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BizumPaymentRequestDTO {
    @NotBlank(message = "El identificador de pago es obligatorio")
    private String paymentId;

    @NotBlank(message = "El identificador de la orden es obligatorio")
    private String orderId;

    // Campo opcional para identificar el usuario (puede ser null si no está logueado)
    private String userId;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "Formato de teléfono inválido (debe tener 9 dígitos)")
    private String phoneNumber;
}
