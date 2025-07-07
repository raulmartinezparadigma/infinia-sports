package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OrderHistoryTest extends BaseTest {

    @BeforeEach
    void setUp() {
        // 1. Login as a user
        login("Vin", "123456");

        // 2. Open the user menu and navigate to "Mis Pedidos"
        page.getByTestId("user-menu-button").click();
        page.getByTestId("my-orders-link").click();

        // 3. Assert that the user is on the order history page
        assertThat(page).hasURL("http://localhost:3000/pedidos");
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Mis Pedidos"))).isVisible();
    }

    @Test
    @DisplayName("Should display order history and detail correctly")
    void shouldDisplayOrderHistoryAndDetailCorrectly() {
        // The setup already navigated to the order history page.
        // 4. Find the first order and extract its data
        Locator firstOrderRow = page.locator("[data-testid^='order-row-']").first();
        assertThat(firstOrderRow).isVisible();

        // Extract order ID from the data-testid attribute of the row
        String rowTestId = firstOrderRow.getAttribute("data-testid");
        String orderId = rowTestId.replace("order-row-", "");

        // Extract just the date part from the text
        String fullDateText = firstOrderRow.getByTestId("order-date").textContent();
        String orderDate = fullDateText.substring(fullDateText.lastIndexOf(' ') + 1);

        // 5. Click to view the order detail
        firstOrderRow.getByTestId("view-order-link").click();

        // 6. Assert that we are on the detail page and the data matches
        assertThat(page.getByTestId("order-detail-heading")).isVisible();
        assertThat(page.getByTestId("order-detail-id")).containsText(orderId);
        assertThat(page.getByTestId("order-detail-date")).containsText(orderDate);
    }
}
