package com.infinia.sports.repository.mongo;

import com.infinia.sports.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para persistencia de pagos en MongoDB
 */
@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    java.util.Optional<Payment> findByOrderId(String orderId);
    // Métodos personalizados si es necesario
}
