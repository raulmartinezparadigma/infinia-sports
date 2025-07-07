package com.infinia.sports.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

public class BaseTest {

    static Playwright playwright;
    static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        // Lanzamos en modo headless para ejecuciones más rápidas en CI, se puede cambiar a false para depurar
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    protected void login(String username, String password) {
        page.navigate("http://localhost:3000/");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Iniciar Sesión")).click();
        page.getByPlaceholder("tu-email@correo.com").fill(username);
        page.getByPlaceholder("tu-contraseña").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Acceder")).click();
    }
}
