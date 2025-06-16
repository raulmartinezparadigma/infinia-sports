package com.infinia.sports.repository.mongo;

import com.infinia.sports.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para operaciones con el carrito de compras
 */
@Repository
public interface CartRepository extends MongoRepository<Cart, String>, CartRepositoryCustom {
    /**
     * Busca un carrito por el ID de usuario
     * @param userId ID del usuario
     * @return Optional con el carrito si existe
     */
    Optional<Cart> findByUserId(String userId);

    /**
     * Busca un carrito por el ID de sesión
     * @param sessionId ID de la sesión
     * @return Optional con el carrito si existe
     */
    Optional<Cart> findBySessionId(String sessionId);

    /**
     * Elimina los carritos asociados a un usuario
     * @param userId ID del usuario
     */
    void deleteByUserId(String userId);

    /**
     * Elimina los carritos asociados a una sesión
     * @param sessionId ID de la sesión
     */
    void deleteBySessionId(String sessionId);


}
