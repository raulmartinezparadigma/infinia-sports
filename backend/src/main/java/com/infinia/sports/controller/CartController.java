package com.infinia.sports.controller;

import com.infinia.sports.model.Cart;
import com.infinia.sports.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar operaciones relacionadas con el carrito
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "cart", description = "API para gestionar el carrito de compras")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    private final CheckoutService checkoutService;
    
    /**
     * Vincula un carrito con un usuario autenticado
     * @param cartId ID del carrito a vincular
     * @return El carrito actualizado
     */
    @Operation(summary = "Vincular carrito con usuario autenticado")
    @PutMapping("/link/{cartId}")
    public ResponseEntity<Cart> linkCartToUser(@PathVariable String cartId) {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // El nombre de usuario es el ID
        
        // Obtener información adicional del usuario si es necesario
        // En este caso, asumimos que el email puede estar en los detalles del usuario
        String userEmail = authentication.getName(); // Por simplicidad usamos el mismo valor
        
        logger.info("[linkCartToUser] Vinculando carrito {} con usuario {}", cartId, userId);
        
        // Llamar al servicio para vincular el carrito con el usuario
        Cart updatedCart = checkoutService.linkCartToUser(cartId, userId, userEmail);
        
        return ResponseEntity.ok(updatedCart);
    }
}
