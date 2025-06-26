package com.infinia.sports.service;

import com.infinia.sports.model.Cart;
import com.infinia.sports.model.Order;
import com.infinia.sports.model.dto.AddressDTO;
import com.infinia.sports.model.dto.CartItemDTO;
import com.infinia.sports.model.dto.CheckoutDTO;
import com.infinia.sports.model.dto.CartDTO;

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
    CartDTO addItemToCart(String sessionId, String userId, CartItemDTO cartItemDTO);

    CartDTO updateCartItemQuantity(String sessionId, String userId, String itemId, Integer quantity);
    
    /**
     * Elimina un producto del carrito
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     * @param itemId ID del producto en el carrito
     * @return El carrito actualizado
     */
    CartDTO removeItemFromCart(String sessionId, String userId, String itemId);
    
    /**
     * Obtiene el contenido del carrito
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     * @return El carrito
     */
    CartDTO getCart(String sessionId, String userId);
    
    /**
     * Guarda las direcciones de envío y facturación
     * @param cartId ID del carrito
     * @param shippingAddress Dirección de envío
     * @param billingAddress Dirección de facturación (opcional)
     * @param sameAsBillingAddress Indica si la dirección de facturación es la misma que la de envío
     * @return El carrito actualizado
     */
    CartDTO saveAddresses(String cartId, AddressDTO shippingAddress, AddressDTO billingAddress, boolean sameAsBillingAddress);
    
    /**
     * Confirma el pedido y lo prepara para el pago
     * @param checkoutDTO Datos del checkout
     * @return La orden creada
     */
    Order confirmOrder(CheckoutDTO checkoutDTO);
    
    /**
     * Elimina todo el carrito (todos los productos) para el usuario o sesión actual
     * @param sessionId ID de la sesión
     * @param userId ID del usuario (opcional)
     */
    void clearCart(String sessionId, String userId);

    /**
     * Vincula un carrito existente con un usuario autenticado
     * @param cartId ID del carrito
     * @param userId ID del usuario autenticado
     * @param userEmail Email del usuario autenticado
     * @return El carrito actualizado
     */
    CartDTO linkCartToUser(String cartId, String userId, String userEmail);
}
