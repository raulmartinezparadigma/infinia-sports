package com.infinia.sports.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String postalCode;

    @NotBlank(message = "El país es obligatorio")
    private String country;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean mainAddress = true;
}
