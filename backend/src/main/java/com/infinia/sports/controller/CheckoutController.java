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
    
    /**
     * Añade un producto al carrito
     */
    @PostMapping("/carrito/items")
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
        
        // Obtener ID de sesión o generar uno nuevo
        String sessionId = getOrCreateSessionId(request);
        
        Cart updatedCart = checkoutService.addItemToCart(sessionId, userId, cartItemDTO);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Elimina un producto del carrito
     */
    @DeleteMapping("/carrito/items/{id}")
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
        
        // Obtener ID de sesión
        String sessionId = getOrCreateSessionId(request);
        
        Cart updatedCart = checkoutService.removeItemFromCart(sessionId, userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }
    
    /**
     * Obtiene el contenido del carrito
     */
    @GetMapping("/carrito")
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
        Order order = checkoutService.confirmOrder(checkoutDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
    
    /**
     * Obtiene información de un pedido
     */
    @GetMapping("/pedidos/{id}")
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
        // Intentar obtener el ID de sesión de la cookie
        String sessionId = null;
        
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("SESSION_ID".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        
        // Si no hay ID de sesión, crear uno nuevo
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        return sessionId;
    }
}
