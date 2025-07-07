package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OrderHistoryTest extends BaseTest {

    @Test
    @DisplayName("Should navigate to order history page after login")
    void shouldNavigateToOrderHistory() {
        // 1. Login as a user
        login("Vin", "password123456");

        // 2. Click on "Mis Pedidos" link
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mis Pedidos")).click();

        // 3. Assert that the user is on the order history page
        // Check for the main heading of the page
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Mis Pedidos"));
        assertThat(heading).isVisible();

        // Optionally, check for the presence of the orders table
        Locator ordersTable = page.getByRole(AriaRole.TABLE);
        assertThat(ordersTable).isVisible();
    }

    @Test
    @DisplayName("Should navigate to order detail page from history")
    void shouldNavigateToOrderDetail() {
        // 1. Login and navigate to order history
        login("Vin", "password123456");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mis Pedidos")).click();

        // 2. Click on the "Ver Pedido" link for the first order in the table
        // We target the first link with this specific text within the table body
        Locator viewOrderLink = page.locator("tbody >> text=Ver Pedido").first();
        assertThat(viewOrderLink).isVisible();
        viewOrderLink.click();

        // 3. Assert that the user is on the order detail page
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Detalles del Pedido"));
        assertThat(heading).isVisible();

        // Also check for a specific element, like the shipping address section
        assertThat(page.getByText("Dirección de Envío")).isVisible();
    }

    @Test
    @DisplayName("Should display correct data on order detail page")
    void shouldDisplayCorrectOrderDetailData() {
        // 1. Login and navigate to order history
        login("Vin", "password123456");
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mis Pedidos")).click();

        // 2. Get data from the first order in the history table
        Locator firstRow = page.locator("tbody tr").first();
        String orderId = firstRow.locator("td").nth(0).textContent();
        String orderDate = firstRow.locator("td").nth(2).textContent(); // Assuming date is the 3rd column

        // 3. Click on the "Ver Pedido" link for that order
        firstRow.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Ver Pedido")).click();

        // 4. Assert that the data on the detail page matches the data from the history page
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Detalles del Pedido"));
        assertThat(heading).isVisible();

        // Check that the order ID in the details contains the ID from the list
        assertThat(page.locator(".order-summary")).containsText(orderId);
        // Check that the shipping info contains the date from the list
        assertThat(page.locator(".shipping-info")).containsText(orderDate);
    }
}
