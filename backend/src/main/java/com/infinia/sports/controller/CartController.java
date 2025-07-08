package com.infinia.sports.controller;

import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar operaciones relacionadas con el carrito
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "cart", description = "API para gestionar el carrito de compras")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    
    private final CheckoutService checkoutService;

    /**
     * Añade un producto al carrito
     */
    @PostMapping("/items")
    @Operation(summary = "Añadir producto al carrito", description = "Añade un producto al carrito de compras")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Producto añadido correctamente",
            content = @Content(schema = @Schema(implementation = CartDTO.class))), @ApiResponse(responseCode = "400", description = "Datos inválidos") })
    public ResponseEntity<CartDTO> addItemToCart(@Valid @RequestBody CartItemDTO cartItemDTO, @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {

        logger.info("[addItemToCart] Parámetros recibidos: cartItemDTO={}, userId={}, sessionId={}", cartItemDTO, userId, request.getSession().getId());
        String sessionId = getOrCreateSessionId(request);
        try {
            CartDTO updatedCart = checkoutService.addItemToCart(sessionId, userId, cartItemDTO);
            logger.info("[addItemToCart] CartDTO devuelto: {}", updatedCart);
            logger.info("[addItemToCart] Respuesta: {}", updatedCart);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            logger.error("[addItemToCart] Error al añadir item al carrito", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @PutMapping("/items/{id}")
    @Operation(summary = "Actualizar cantidad de producto en el carrito", description = "Actualiza la cantidad de un producto en el carrito de compras")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente",
            content = @Content(schema = @Schema(implementation = CartDTO.class))), @ApiResponse(responseCode = "404",
            description = "Producto no encontrado en el carrito") })
    public ResponseEntity<CartDTO> updateCartItemQuantity(@PathVariable("id") String itemId, @Valid @RequestBody CartItemDTO cartItemDTO,
            @RequestHeader(value = "User-ID", required = false) String userId, HttpServletRequest request) {
        logger.info("[updateCartItemQuantity] Parámetros recibidos: itemId={}, quantity={}, userId={}, sessionId={}", itemId, cartItemDTO.getQuantity(), userId,
                request.getSession().getId());
        String sessionId = getOrCreateSessionId(request);
        try {
            CartDTO updatedCart = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, cartItemDTO.getQuantity());
            logger.info("[updateCartItemQuantity] CartDTO devuelto: {}", updatedCart);
            logger.info("[updateCartItemQuantity] Respuesta del servicio: {}", updatedCart);
            return ResponseEntity.ok(updatedCart);
        } catch (com.infinia.sports.exception.ResourceNotFoundException e) {
            logger.warn("[updateCartItemQuantity] Carrito o item no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("[updateCartItemQuantity] Error al actualizar cantidad de item del carrito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina un producto del carrito
     */
    @DeleteMapping("/items/{id}")
    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito de compras")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente",
            content = @Content(schema = @Schema(implementation = CartDTO.class))), @ApiResponse(responseCode = "404",
            description = "Producto no encontrado en el carrito") })
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable("id") String itemId, @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {

        logger.info("[removeItemFromCart] Parámetros recibidos: itemId={}, userId={}, sessionId={}", itemId, userId, request.getSession().getId());
        String sessionId = getOrCreateSessionId(request);
        try {
            logger.info("[removeItemFromCart] Llamando a checkoutService.removeItemFromCart...");
            CartDTO updatedCart = checkoutService.removeItemFromCart(sessionId, userId, itemId);
            logger.info("[removeItemFromCart] CartDTO devuelto: {}", updatedCart);
            logger.info("[removeItemFromCart] Respuesta del servicio: {}", updatedCart);
            return ResponseEntity.ok(updatedCart);
        } catch (com.infinia.sports.exception.ResourceNotFoundException e) {
            logger.warn("[removeItemFromCart] Carrito no encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("[removeItemFromCart] Error al eliminar item del carrito", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina todo el carrito del usuario o sesión actual
     */
    @DeleteMapping
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito del usuario o sesión actual")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente"), @ApiResponse(responseCode = "400",
            description = "No se pudo vaciar el carrito") })
    public ResponseEntity<Void> clearCart(@RequestHeader(value = "User-ID", required = false) String userId, HttpServletRequest request) {
        String sessionId = getOrCreateSessionId(request);
        logger.info("[clearCart] Endpoint llamado. userId={}, sessionId={}", userId, sessionId);
        try {
            checkoutService.clearCart(sessionId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("[clearCart] Error al vaciar el carrito: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene el contenido del carrito
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Obtiene el contenido del carrito de compras")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente",
            content = @Content(schema = @Schema(implementation = CartDTO.class))), @ApiResponse(responseCode = "404", description = "Carrito no encontrado") })
    public ResponseEntity<CartDTO> getCart(@RequestHeader(value = "User-ID", required = false) String userId, HttpServletRequest request) {

        // Obtener ID de sesión
        String sessionId = getOrCreateSessionId(request);

        try {
            CartDTO cart = checkoutService.getCart(sessionId, userId);
            logger.info("[getCart] CartDTO devuelto: {}", cart);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            // Si no existe carrito, devolver uno vacío
            CartDTO cart = CartDTO.builder().sessionId(sessionId).userId(userId).build();
            logger.info("[getCart] CartDTO devuelto: {}", cart);
            return ResponseEntity.ok(cart);
        }
    }

    /**
     * Vincula un carrito con un usuario autenticado
     * @param cartId ID del carrito a vincular
     * @return El carrito actualizado
     */
    @Operation(summary = "Vincular carrito con usuario autenticado")
    @PutMapping("/link/{cartId}")
    public ResponseEntity<CartDTO> linkCartToUser(@PathVariable String cartId) {
        // Obtener el usuario autenticado del contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // El nombre de usuario es el ID
        
        // Obtener información adicional del usuario si es necesario
        // En este caso, asumimos que el email puede estar en los detalles del usuario
        String userEmail = authentication.getName(); // Por simplicidad usamos el mismo valor
        
        logger.info("[linkCartToUser] Vinculando carrito {} con usuario {}", cartId, userId);
        
        // Llamar al servicio para vincular el carrito con el usuario
        CartDTO dto = checkoutService.linkCartToUser(cartId, userId, userEmail);
        return ResponseEntity.ok(dto);
    }

    /**
     * Obtiene o crea un ID de sesión
     */
    private String getOrCreateSessionId(HttpServletRequest request) {
        // Usa la sesión HTTP estándar (JSESSIONID)
        return request.getSession(true).getId();
    }
}
