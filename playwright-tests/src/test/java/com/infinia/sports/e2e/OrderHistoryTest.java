package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OrderHistoryTest extends BaseTest {
    

    @Test
    @DisplayName("Debería navegar a historial de pedidos y verificar que no hay pedidos")
    void shouldNavigateToOrderHistoryAndVerifyNoOrders() {
        login("testinfinia", "123456");

        page.getByTestId("user-menu-button").click();
        page.getByTestId("my-orders-link").click();

        // Verificar que estamos en la página correcta
        assertThat(page).hasURL("http://localhost:3000/pedidos");

        // Verificar el mensaje específico que indica que no hay pedidos usando data-testid
        Locator noOrdersMessage = page.getByTestId("no-orders-message");
        assertThat(noOrdersMessage).isVisible();
        // Verificar también el contenido del mensaje
        assertThat(noOrdersMessage).containsText("No tienes pedidos realizados todavía.");
        
        // Adicionalmente, verificar que no existen elementos de pedido
        Locator orderCards = page.locator("[data-testid^='order-card-']");
        assertThat(orderCards).hasCount(0);
    }
}
