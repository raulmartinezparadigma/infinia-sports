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

    @Test
    @DisplayName("Should display the added product in the cart page")
    void shouldDisplayProductInCart() {
        // 1. Navigate to the homepage and click the first product
        page.navigate("http://localhost:3000/");
        page.locator(".product-card a").first().click();

        // 2. Get the product name and add it to the cart
        String productName = page.locator("h1").textContent();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Añadir al carrito")).click();

        // 3. Navigate to the cart page
        page.locator("a[href='/carrito']").click();

        // 4. Assert that the product is visible in the cart
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Mi Carrito"))).isVisible();
        assertThat(page.getByText(productName)).isVisible();
    }
}
