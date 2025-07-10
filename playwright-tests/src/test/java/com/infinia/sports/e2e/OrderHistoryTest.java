package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OrderHistoryTest extends BaseTest {

    @Test
    @DisplayName("Should navigate to order history and see orders")
    void shouldNavigateToOrderHistory() {
        login("testinfinia", "123456");

        page.getByTestId("user-menu-button").click();
        page.getByTestId("my-orders-link").click();

        assertThat(page).hasURL("http://localhost:3000/pedidos");

        Locator firstOrderCard = page.getByTestId("order-card-1").first();
        assertThat(firstOrderCard).isVisible();
    }

    @Test
    @DisplayName("Should navigate to order detail and verify data consistency")
    void shouldNavigateToOrderDetailAndVerifyData() {
        login("testinfinia", "123456");
        page.getByTestId("user-menu-button").click();
        page.getByTestId("my-orders-link").click();

        Locator firstOrderCard = page.getByTestId("order-card-1").first();
        String orderId = firstOrderCard.getByTestId("order-id-1").textContent().replace("ID: ", "").trim();
        String orderDate = firstOrderCard.getByTestId("order-date-1").textContent().replace("Pedido entregado el ", "").trim();

        firstOrderCard.getByTestId("view-order-button-1").click();

        assertThat(page).hasURL("http://localhost:3000/pedidos/" + orderId);

        Locator orderDetailId = page.getByTestId("order-detail-id");
        assertThat(orderDetailId).containsText(orderId);

        Locator orderDetailDate = page.getByTestId("order-detail-date");
        assertThat(orderDetailDate).hasText(orderDate);
    }
}
