package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Should allow a user to log in successfully")
    void shouldLoginSuccessfully() {
        // 1. Use the login helper from BaseTest
        login("Vin", "password123456");

        // 2. Assert that login was successful
        // Check that "Mis Pedidos" link is now visible
        Locator myOrdersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Mis Pedidos"));
        assertThat(myOrdersLink).isVisible();

        // Check that a logout button is visible
        Locator logoutButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cerrar Sesión"));
        assertThat(logoutButton).isVisible();

        // Check that the initial login button is no longer visible
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Iniciar Sesión"));
        assertThat(loginButton).not().isVisible();
    }
}
