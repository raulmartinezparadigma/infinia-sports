package com.infinia.sports.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.CheckoutService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para la gestión del proceso de checkout
 */
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Tag(name = "checkout", description = "API para la gestión del proceso de checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    /**
     * Guarda la dirección de envío/facturación
     */
    @PostMapping("/direccion")
    @Operation(summary = "Guardar dirección", description = "Guarda la dirección de envío y facturación")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Dirección guardada correctamente",
            content = @Content(schema = @Schema(implementation = CartDTO.class))), @ApiResponse(responseCode = "400",
            description = "Datos inválidos"), @ApiResponse(responseCode = "404", description = "Carrito no encontrado") })
    public ResponseEntity<CartDTO> saveAddresses(@RequestParam("cartId") String cartId, @Valid @RequestBody AddressDTO shippingAddress,
            @RequestBody(required = false) AddressDTO billingAddress,
            @RequestParam(value = "sameAsBillingAddress", defaultValue = "false") boolean sameAsBillingAddress) {

        CartDTO updatedCart = checkoutService.saveAddresses(cartId, shippingAddress, billingAddress, sameAsBillingAddress);
        logger.info("[saveAddresses] CartDTO devuelto: {}", updatedCart);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * Confirma el pedido y lo prepara para pago
     */
    @PostMapping("/confirm")
    @Operation(summary = "Confirm order", description = "Confirms the order and prepares it for payment")
    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderDTO.class))), @ApiResponse(responseCode = "400",
            description = "Invalid data"), @ApiResponse(responseCode = "404", description = "Cart not found") })
    public ResponseEntity<OrderDTO> confirmOrder(@Valid @RequestBody CheckoutDTO checkoutDTO) {
        logger.info("[confirmOrder] Calling confirmOrder", checkoutDTO.getCartId());
        try {
            OrderDTO orderDTO = checkoutService.confirmOrder(checkoutDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
        } catch (Exception e) {
            logger.error("[confirmOrder] Error confirming the order", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
