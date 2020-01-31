package io.github.lucysuslova;

import com.codeborne.selenide.SelenideElement;
import io.github.lucysuslova.extensions.SetupExtension;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.page.Page;
import org.openqa.selenium.devtools.page.model.Viewport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.devtools.page.Page.CaptureScreenshotFormat.PNG;

@ExtendWith(SetupExtension.class)
public class DevToolsScreenshotTest {

    private DevTools chromeDevTools;
    private final String gitHubUrl = "https://github.com/";
    private final String fileName = "github_screenshot.png";

    @BeforeEach
    public void setup() {
        open(gitHubUrl);
        ChromeDriver driver = (ChromeDriver) getWebDriver();
        chromeDevTools = driver.getDevTools();
        chromeDevTools.createSession();
    }

    @AfterEach
    public void cleanup() throws IOException {
        Path path = Paths.get(fileName);
        Files.deleteIfExists(path);
    }

    @Test
    public void elementScreenshotTest() throws IOException {
        SelenideElement logo = $("header .octicon-mark-github");
        Viewport viewport = new Viewport(logo.getLocation().getX(), logo.getLocation().getY(), logo.getSize().getWidth(), logo.getSize().getHeight(), 1);

        String image = chromeDevTools.send(Page.captureScreenshot(Optional.of(PNG), Optional.empty(), Optional.of(viewport), Optional.empty()));
        writeToFile(image, fileName);

        Path content = Paths.get(fileName);
        assertEquals(content.getFileName().toString(), fileName);

        try (InputStream is = Files.newInputStream(content)) {
            Allure.addAttachment("Screenshot_Devtools", is);
        }
    }

    private void writeToFile(String encodedString, String outputFileName) throws IOException {
        byte[] decodedBytes = Base64.getDecoder()
                .decode(encodedString.getBytes(StandardCharsets.UTF_8));
        FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
    }

}
