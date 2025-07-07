package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShoppingCartTest extends BaseTest {

    @Test
    void shouldAddProductToCartAndVerify() {
        // Navigate to the catalog
        page.navigate("/");

        // Find the first product card that appears on the page
        Locator firstProductCard = page.locator("[data-testid^='product-card-']").first();
        assertThat(firstProductCard).isVisible();

        // Extract product name and ID for verification later
        String productName = firstProductCard.getByTestId("product-name").textContent();
        String dataTestId = firstProductCard.getAttribute("data-testid");
        String productId = dataTestId.replace("product-card-", "");

        // Click the "Add to cart" button within that card
        firstProductCard.getByTestId("add-to-cart-button").click();

        // Click on the cart icon in the navbar
        page.getByTestId("cart-link").click();

        // Verify that we are on the cart page by checking the URL and a unique element
        assertThat(page).hasURL("http://localhost:3000/cart");
        assertThat(page.getByTestId("cart-view")).isVisible();

        // Verify that the correct product is in the cart using the extracted ID
        Locator cartItem = page.getByTestId("cart-item-" + productId);
        assertThat(cartItem).isVisible();
        assertThat(cartItem.getByTestId("cart-item-name")).hasText(productName);
    }
}
