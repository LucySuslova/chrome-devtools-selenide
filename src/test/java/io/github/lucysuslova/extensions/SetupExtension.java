package io.github.lucysuslova.extensions;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.codeborne.selenide.Selenide.clearBrowserCookies;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class SetupExtension implements BeforeEachCallback, AfterEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Configuration.browser = "chrome";
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().savePageSource(false).screenshots(true));
        clearBrowserCookies();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        getWebDriver().quit();
    }
}
