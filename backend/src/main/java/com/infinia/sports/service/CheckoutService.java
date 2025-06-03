package com.infinia.sports.service;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;

/**
 * Interfaz para el servicio de checkout
 */
public interface CheckoutService {
    
    /**
     * Añade un producto al carrito
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     * @param cartItemDTO Datos del producto a añadir
     * @return El carrito actualizado
     */
    Cart addItemToCart(String sessionId, String userId, CartItemDTO cartItemDTO);
    
    /**
     * Elimina un producto del carrito
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     * @param itemId ID del producto en el carrito
     * @return El carrito actualizado
     */
    Cart removeItemFromCart(String sessionId, String userId, String itemId);
    
    /**
     * Obtiene el contenido del carrito
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     * @return El carrito
     */
    Cart getCart(String sessionId, String userId);
    
    /**
     * Guarda las direcciones de envío y facturación
     * @param cartId ID del carrito
     * @param shippingAddress Dirección de envío
     * @param billingAddress Dirección de facturación (opcional)
     * @param sameAsBillingAddress Indica si la dirección de facturación es la misma que la de envío
     * @return El carrito actualizado
     */
    Cart saveAddresses(String cartId, AddressDTO shippingAddress, AddressDTO billingAddress, boolean sameAsBillingAddress);
    
    /**
     * Confirma el pedido y lo prepara para el pago
     * @param checkoutDTO Datos del checkout
     * @return La orden creada
     */
    Order confirmOrder(CheckoutDTO checkoutDTO);
    
    /**
     * Obtiene información de un pedido
     * @param orderId ID del pedido
     * @return La orden
     */
    Order getOrder(String orderId);
}
