package com.infinia.sports.service;

import com.infinia.sports.model.dto.OrderDTO;
import java.util.List;

/**
 * Servicio para gestión de pedidos
 */
public interface OrderService {
    /**
     * Obtiene un pedido por su ID
     * 
     * @param orderId ID del pedido a consultar
     * @return DTO con la información completa del pedido
     */
    OrderDTO getOrderById(String orderId);
    
    /**
     * Obtiene todos los pedidos asociados a un email de usuario
     * 
     * @param email Email del usuario para filtrar los pedidos
     * @return Lista de DTOs con la información de los pedidos
     */
    List<OrderDTO> getOrdersByEmail(String email);
}
