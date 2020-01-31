package io.github.lucysuslova;

import com.codeborne.selenide.Condition;
import com.google.common.collect.ImmutableMap;
import io.github.lucysuslova.extensions.SetupExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.log.Log;
import org.openqa.selenium.devtools.network.Network;
import org.openqa.selenium.devtools.network.model.Headers;

import java.util.Optional;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.devtools.network.Network.emulateNetworkConditions;

@ExtendWith(SetupExtension.class)
public class DevToolsTest {

    private DevTools chromeDevTools;
    private final String imdbUrl = "https://www.imdb.com/";

    @BeforeEach
    public void setup() {
        open(imdbUrl);
        ChromeDriver driver = (ChromeDriver) getWebDriver();
        chromeDevTools = driver.getDevTools();
        chromeDevTools.createSession();
    }

    @Test
    public void goOfflineTest() {
        chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        chromeDevTools.send(
                emulateNetworkConditions(true, 0, 0, 0, Optional.empty()));
        open(imdbUrl);
        $(".error-code").shouldHave(Condition.text("ERR_INTERNET_DISCONNECTED"));
    }

    @Test
    public void webToMobileTest() {
        String mAgent = "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Mobile Safari/537.36";
        chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        chromeDevTools.send(Network.setUserAgentOverride(mAgent, Optional.empty(), Optional.empty()));
        open(imdbUrl);
        assertEquals("https://m.imdb.com/", getWebDriver().getCurrentUrl(), "Opened imdb version is not mobile");
    }

    @Test
    public void addCustomHeaderTest() {
        chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
        chromeDevTools.send(Network.setExtraHTTPHeaders(new Headers(ImmutableMap.of("testCustomHeader",
                "testCustomHeaderValue"))));
        chromeDevTools.addListener(Network.requestWillBeSent(), requestWillBeSent ->
                assertEquals(requestWillBeSent.getRequest().getHeaders().get("testCustomHeader"),
                        "testCustomHeaderValue"));
        open(imdbUrl);
    }

    @Test
    public void consoleMessageTest() {
        String message = "See this message in console?";
        chromeDevTools.send(Log.enable());
        chromeDevTools.addListener(Log.entryAdded(), consoleMessageFromDevTools ->
                assertEquals(consoleMessageFromDevTools.getText(), message));
        open(imdbUrl);
        executeJavaScript("console.log('" + message + "');");
    }

}
