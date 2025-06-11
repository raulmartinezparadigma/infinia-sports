package com.infinia.sports.controller;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador para la gestión del carrito y el proceso de checkout
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Checkout", description = "API para la gestión del carrito y el proceso de checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CheckoutController.class);
    
    /**
     * Añade un producto al carrito
     */
    @PostMapping("/cart/items")
    @Operation(summary = "Añadir producto al carrito", description = "Añade un producto al carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto añadido correctamente", 
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Cart> addItemToCart(
            @Valid @RequestBody CartItemDTO cartItemDTO,
            @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {
        
        // Log de parámetros de entrada
        logger.info("[addItemToCart] Parámetros recibidos: cartItemDTO={}, userId={}, sessionId={}", cartItemDTO, userId, request.getSession().getId());
        // Obtener ID de sesión o generar uno nuevo
        String sessionId = getOrCreateSessionId(request);
        
        Cart updatedCart = checkoutService.addItemToCart(sessionId, userId, cartItemDTO);
        logger.info("[addItemToCart] Respuesta: {}", updatedCart);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    @PutMapping("/cart/items/{id}")
    @Operation(summary = "Actualizar cantidad de producto en el carrito", description = "Actualiza la cantidad de un producto en el carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable("id") String itemId,
            @Valid @RequestBody CartItemDTO cartItemDTO,
            @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {
        logger.info("[updateCartItemQuantity] Parámetros recibidos: itemId={}, quantity={}, userId={}, sessionId={}", itemId, cartItemDTO.getQuantity(), userId, request.getSession().getId());
        String sessionId = getOrCreateSessionId(request);
        try {
            Cart updatedCart = checkoutService.updateCartItemQuantity(sessionId, userId, itemId, cartItemDTO.getQuantity());
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
    @DeleteMapping("/cart/items/{id}")
    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un producto del carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente", 
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en el carrito")
    })
    public ResponseEntity<Cart> removeItemFromCart(
            @PathVariable("id") String itemId,
            @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {
        
        logger.info("[removeItemFromCart] Parámetros recibidos: itemId={}, userId={}, sessionId={}", itemId, userId, request.getSession().getId());
        String sessionId = getOrCreateSessionId(request);
        try {
            logger.info("[removeItemFromCart] Llamando a checkoutService.removeItemFromCart...");
            Cart updatedCart = checkoutService.removeItemFromCart(sessionId, userId, itemId);
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
    @DeleteMapping("/cart")
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito del usuario o sesión actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente"),
            @ApiResponse(responseCode = "400", description = "No se pudo vaciar el carrito")
    })
    public ResponseEntity<Void> clearCart(
            @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {
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
    @GetMapping("/cart")
    @Operation(summary = "Obtener carrito", description = "Obtiene el contenido del carrito de compras")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente", 
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    public ResponseEntity<Cart> getCart(
            @RequestHeader(value = "User-ID", required = false) String userId,
            HttpServletRequest request) {
        
        // Obtener ID de sesión
        String sessionId = getOrCreateSessionId(request);
        
        try {
            Cart cart = checkoutService.getCart(sessionId, userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            // Si no existe carrito, devolver uno vacío
            return ResponseEntity.ok(Cart.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .build());
        }
    }
    
    /**
     * Guarda la dirección de envío/facturación
     */
    @PostMapping("/checkout/direccion")
    @Operation(summary = "Guardar dirección", description = "Guarda la dirección de envío y facturación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dirección guardada correctamente", 
                    content = @Content(schema = @Schema(implementation = Cart.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    public ResponseEntity<Cart> saveAddresses(
            @RequestParam("cartId") String cartId,
            @Valid @RequestBody AddressDTO shippingAddress,
            @RequestBody(required = false) AddressDTO billingAddress,
            @RequestParam(value = "sameAsBillingAddress", defaultValue = "false") boolean sameAsBillingAddress) {
        
        Cart updatedCart = checkoutService.saveAddresses(cartId, shippingAddress, billingAddress, sameAsBillingAddress);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Confirma el pedido y lo prepara para pago
     */
    @PostMapping("/checkout/confirmar")
    @Operation(summary = "Confirmar pedido", description = "Confirma el pedido y lo prepara para el pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente", 
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    public ResponseEntity<Order> confirmOrder(@Valid @RequestBody CheckoutDTO checkoutDTO) {
        logger.info("[confirmOrder] Llamamos al confirmOrder", checkoutDTO.getCartId());

        Order order = checkoutService.confirmOrder(checkoutDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    /**
     * Obtiene información de un pedido
     */
    @GetMapping("/orders/{id}")
    @Operation(summary = "Obtener pedido", description = "Obtiene información de un pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido obtenido correctamente", 
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Order> getOrder(@PathVariable("id") String orderId) {
        Order order = checkoutService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }
    
    /**
     * Obtiene o crea un ID de sesión
     */
    private String getOrCreateSessionId(HttpServletRequest request) {
        // Usa la sesión HTTP estándar (JSESSIONID)
        return request.getSession(true).getId();
    }
}
