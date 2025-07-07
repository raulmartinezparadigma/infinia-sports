package com.infinia.sports.e2e;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest extends BaseTest {

    @Test
    @DisplayName("Should allow a user to log in successfully")
    void shouldLoginSuccessfully() {
        // 1. Use the login helper from BaseTest with the correct credentials
        login("Vin", "123456");

        // 2. Assert that login was successful by finding the logout button
        // First, click the user menu to make the logout button visible
        page.getByTestId("user-menu-button").click();

        // Now, find the logout button by its test id and assert it's visible
        Locator logoutButton = page.getByTestId("logout-button");
        assertThat(logoutButton).isVisible();
    }
}
