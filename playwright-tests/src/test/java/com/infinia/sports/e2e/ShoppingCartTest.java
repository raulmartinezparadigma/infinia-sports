package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShoppingCartTest extends BaseTest {

    @Test
    @DisplayName("Should add a product to the cart from product detail page")
    void shouldAddProductToCart() {
        // 1. Navigate to the homepage
        page.navigate("http://localhost:3000/");

        // 2. Click on the first product to go to its detail page
        Locator firstProduct = page.locator(".product-card a").first();
        assertThat(firstProduct).isVisible();
        firstProduct.click();

        // 3. Click the 'Add to Cart' button
        Locator addToCartButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Añadir al carrito"));
        assertThat(addToCartButton).isEnabled();
        addToCartButton.click();

        // 4. Assert that the cart icon in the header now shows '1'
        Locator cartIcon = page.locator("a[href='/carrito']");
        assertThat(cartIcon).containsText("1");
    }
}
