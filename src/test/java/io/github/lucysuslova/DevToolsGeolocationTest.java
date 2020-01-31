package io.github.lucysuslova;

import io.github.lucysuslova.extensions.SetupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.emulation.Emulation;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SetupExtension.class)
public class DevToolsGeolocationTest {

    private DevTools chromeDevTools;
    private final String mapsUrl = "https://www.google.com/maps/";

    @BeforeEach
    public void setup() {
        open(mapsUrl);
        ChromeDriver driver = (ChromeDriver) getWebDriver();
        chromeDevTools = driver.getDevTools();
        chromeDevTools.createSession();
    }

    @Test
    public void fakeLocationTest() {
        Number latitude = 37.422290;
        Number longitude = -122.08405;

        $("h1").shouldBe(visible);

        chromeDevTools.send(
                Emulation.setGeolocationOverride(Optional.of(latitude), Optional.of(longitude), Optional.of(100)));

        sleep(2000);
        open(mapsUrl);
        sleep(2000);

        $("#widget-mylocation").click();
        waitForUrlChanged(4000);

        assertAll(() -> {
            assertTrue(getWebDriver().getCurrentUrl().contains(longitude.toString()),
                    "Opened url does not contain fake longitude");
            assertTrue(getWebDriver().getCurrentUrl().contains(latitude.toString()),
                    "Opened url does not contain fake latitude");
        });

    }

    private void waitForUrlChanged(long milliseconds) {
        new WebDriverWait(getWebDriver(), Duration.ofMillis(milliseconds))
                .until(ExpectedConditions.urlContains("@"));
    }


}
