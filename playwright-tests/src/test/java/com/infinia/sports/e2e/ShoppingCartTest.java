package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShoppingCartTest extends BaseTest {

    @Test
    @DisplayName("Should add a product and verify it in the cart")
    void shouldAddProductAndVerifyInCart() {
        login("testinfinia", "123456");
        page.navigate("http://localhost:3000/");

        // Locate the first product card using a resilient selector that finds any product card,
        // rather than relying on a hardcoded ID.
        Locator productCard = page.locator("[data-testid^='product-card-']").first();
        assertThat(productCard).isVisible();

        // Within the product card, find and click the add-to-cart button.
        Locator addToCartButton = productCard.getByTestId("add-to-cart-button");
        addToCartButton.click();

        // Verify the cart count increases.
        Locator cartBadge = page.locator(".MuiBadge-badge");
        assertThat(cartBadge).hasText("1");
    }
}
