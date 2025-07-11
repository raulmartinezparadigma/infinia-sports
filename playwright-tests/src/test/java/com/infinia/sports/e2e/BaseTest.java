package com.infinia.sports.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BaseTest {

    static Playwright playwright;
    static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        // Lanzamos en modo headless para ejecuciones más rápidas en CI, se puede cambiar a false para depurar
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();

        // Listener para logs de la consola del navegador
        page.onConsoleMessage(msg -> {
            System.out.println("[Browser Console] " + msg.type().toUpperCase() + ": " + msg.text());
        });

        // Listener para fallos en las peticiones de red
        page.onRequestFailed(request -> {
            System.out.println("[Network Error] Failed request: " + request.url() + " (" + request.failure() + ")");
        });

        page.navigate("http://localhost:3000");
        page.waitForSelector("#root"); // Espera a que la app React se monte en el DOM
    }

    @AfterEach
    void closeContext() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
    }

    protected void login(String username, String password) {
        // 1. Abrir modal de login
        page.getByTestId("login-button").click();

        // 2. Rellenar credenciales y enviar el formulario
        page.getByTestId("username-input").fill(username);
        page.getByTestId("password-input").fill(password);
        page.getByTestId("submit-login-button").click();

        // 3. Verificar que el menú de usuario es visible tras el login
        assertThat(page.getByTestId("user-menu-button")).isVisible();

        // 4. Abrir el menú para verificar el botón de logout
        page.getByTestId("user-menu-button").click();
        assertThat(page.getByTestId("logout-button")).isVisible();

        // 5. CERRAR el menú para evitar que el backdrop interfiera con tests siguientes
        page.keyboard().press("Escape");
    }
}
