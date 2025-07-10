package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Should allow a user to log in successfully")
    void shouldLoginSuccessfully() {
        login("testinfinia", "123456");

        // First, verify the user menu button is visible, which indicates a successful login.
        Locator userMenuButton = page.getByTestId("user-menu-button");
        assertThat(userMenuButton).isVisible();

        // The login button should no longer be visible.
        Locator loginButton = page.getByTestId("login-button");
        assertThat(loginButton).not().isVisible();

        // Now, click the user menu to reveal the dropdown items.
        userMenuButton.click();

        // Verify that the links inside the menu are visible.
        Locator myOrdersLink = page.getByTestId("my-orders-link");
        assertThat(myOrdersLink).isVisible();

        Locator logoutButton = page.getByTestId("logout-button");
        assertThat(logoutButton).isVisible();
    }
}
