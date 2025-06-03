package com.infinia.sports.repository;

import com.infinia.sports.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones con órdenes
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    /**
     * Busca una orden por su orderId
     * @param orderId ID de la orden
     * @return Optional con la orden si existe
     */
    Optional<Order> findByOrderId(String orderId);
    
    /**
     * Busca órdenes por el email del cliente
     * @param email Email del cliente
     * @return Lista de órdenes
     */
    List<Order> findByEmail(String email);
    
    /**
     * Busca órdenes por estado
     * @param status Estado de la orden
     * @return Lista de órdenes
     */
    List<Order> findByStatus(String status);
    
    /**
     * Busca órdenes creadas entre dos fechas
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Lista de órdenes
     */
    List<Order> findBySubmitDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
