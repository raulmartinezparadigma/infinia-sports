package com.infinia.sports.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "orders", description = "API para gestión de pedidos del usuario")
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Obtiene información de un pedido específico por su ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener información de un pedido", 
              description = "Obtiene la información completa de un pedido específico por su ID, incluyendo productos, dirección de envío y resumen de costes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<OrderDTO> getOrder(
            @Parameter(description = "ID del pedido a consultar", required = true) 
            @PathVariable("id") String orderId) {
        try {
            OrderDTO dto = orderService.getOrderById(orderId);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            logger.warn("[getOrder] Pedido no encontrado para orderId: {}. Devolviendo 404.", orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("[getOrder] Error inesperado al obtener el pedido para orderId: {}. Error: {}", orderId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene los pedidos de un usuario por su email
     */
    @GetMapping
    @Operation(summary = "Obtener pedidos por email", 
              description = "Obtiene todos los pedidos asociados a un email de usuario para mostrar en la sección 'Mis Pedidos'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Parámetro email no válido o no proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<OrderDTO>> getOrdersByEmail(
            @Parameter(description = "Email del usuario para filtrar pedidos", required = true) 
            @RequestParam("email") String email) {
        try {
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            List<OrderDTO> orders = orderService.getOrdersByEmail(email);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("[getOrdersByEmail] Error inesperado al obtener pedidos para email: {}. Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
