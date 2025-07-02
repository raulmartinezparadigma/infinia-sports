package com.infinia.sports.service.impl;

import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.OrderDTO;
import com.infinia.sports.mapper.OrderMapper;
import com.infinia.sports.repository.mongo.OrderRepository;
import com.infinia.sports.repository.jpa.ProductRepository;
import com.infinia.sports.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderDTO getOrderById(String orderId) {
        try {
            logger.info("[OrderService] Buscando pedido con orderId: {}", orderId);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
            // Hidratación correcta: lineItems están dentro de cada shippingGroup
            if (order.getShippingGroups() != null) {
                order.getShippingGroups().forEach(shippingGroup -> {
                    if (shippingGroup.getLineItems() != null) {
                        shippingGroup.getLineItems().forEach(lineItem -> {
                            if (lineItem.getProductId() != null) {
                                try {
                                    UUID productId = UUID.fromString(lineItem.getProductId());
                                    productRepository.findById(productId).ifPresent(lineItem::setProduct);
                                } catch (IllegalArgumentException e) {
                                    logger.warn("El productId '{}' no es un UUID válido para el lineItem '{}'", lineItem.getProductId(), lineItem.getId());
                                }
                            }
                        });
                    }
                });
            }
            logger.info("Pedido {} completamente hidratado.", orderId);
            return OrderMapper.toDTO(order);
        } catch (Exception e) {
            logger.error("[OrderService] Error inesperado al obtener el pedido para orderId: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public List<OrderDTO> getOrdersByEmail(String email) {
        try {
            logger.info("[OrderService] Buscando pedidos para el email: {}", email);
            List<Order> orders = orderRepository.findByEmail(email);
            
            // Hidratar los productos en cada pedido
            orders.forEach(order -> {
                if (order.getShippingGroups() != null) {
                    order.getShippingGroups().forEach(shippingGroup -> {
                        if (shippingGroup.getLineItems() != null) {
                            shippingGroup.getLineItems().forEach(lineItem -> {
                                if (lineItem.getProductId() != null) {
                                    try {
                                        UUID productId = UUID.fromString(lineItem.getProductId());
                                        productRepository.findById(productId).ifPresent(lineItem::setProduct);
                                    } catch (IllegalArgumentException e) {
                                        logger.warn("El productId '{}' no es un UUID válido para el lineItem '{}'", lineItem.getProductId(), lineItem.getId());
                                    }
                                }
                            });
                        }
                    });
                }
            });
            
            logger.info("Se encontraron {} pedidos para el email: {}", orders.size(), email);
            return orders.stream()
                    .map(OrderMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("[OrderService] Error inesperado al obtener pedidos para el email: {}. Error: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}
