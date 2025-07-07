package com.infinia.sports.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is a temporary, simplified version of BaseTest for diagnostic purposes.
 * All Playwright-related code has been removed to isolate the test discovery issue.
 */
public class BaseTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(true) // Run in headless mode for standard non-visual test execution
        );
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage(TestInfo testInfo) {
        // Revert to a simpler context creation, removing extra headers for now
        context = browser.newContext(new Browser.NewContextOptions()
            .setBaseURL("http://localhost:3000")
            .setViewportSize(1920, 1080));

        // Increase default timeout to 60 seconds for debugging slow startups
        context.setDefaultTimeout(60000);

        // Add listeners to capture all browser activity for deep debugging
        context.onPage(p -> {
            p.onConsoleMessage(msg -> System.out.println(
                "BROWSER CONSOLE: " + msg.text()));
            p.onRequest(request -> System.out.println(
                ">> Request: " + request.method() + " " + request.url()));
            p.onResponse(response -> System.out.println(
                "<< Response: " + response.status() + " " + response.url()));
        });

        // Smart interceptor to redirect API calls while letting static assets load normally.
        context.route("**/*", route -> {
            String url = route.request().url();

            // This is the key: only treat URLs containing "/api/" as API calls.
            boolean isApiCall = url.contains("/api/");

            if (isApiCall) {
                String newUrl = url.replace("http://localhost:3000", "http://localhost:8080");
                System.out.println("Redirecting API call: " + url + " -> " + newUrl);
                try {
                    // Use route.fetch() to forward the request to the backend
                    APIResponse response = route.fetch(new Route.FetchOptions().setUrl(newUrl));
                    // Fulfill the original request with the response from the backend
                    route.fulfill(new Route.FulfillOptions().setResponse(response));
                } catch (PlaywrightException e) {
                    System.err.println("Error redirecting request to " + newUrl + ": " + e.getMessage());
                    route.abort();
                }
            } else {
                // For all other requests (static assets, initial page load), let them continue to the original destination.
                route.resume();
            }
        });

        // Start tracing before creating the page
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo testInfo) {
        // With the workingDirectory set in pom.xml, this relative path will resolve correctly
        // inside the playwright-tests module directory.
        Path tracePath = Paths.get("playwright-traces", testInfo.getTestMethod().orElseThrow().getName() + ".zip");
        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        context.close();
    }

    protected void login(String email, String password) {
        // Navigate directly to the login page to bypass issues with clicking the link
        page.navigate("http://localhost:3000/login");

        // Use getByLabel for robustness, based on the form's visible labels from the screenshot
        page.getByLabel("Nombre de usuario *").fill(email);
        page.getByLabel("Contraseña *").fill(password);

        // Use a specific CSS selector for the submit button to avoid ambiguity and encoding issues
        page.locator("button[type='submit']").click();
    }
}