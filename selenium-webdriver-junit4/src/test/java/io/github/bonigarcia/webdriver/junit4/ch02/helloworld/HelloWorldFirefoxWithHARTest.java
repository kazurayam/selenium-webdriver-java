package io.github.bonigarcia.webdriver.junit4.ch02.helloworld;

import com.kazurayam.unittest.TestHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;


public class HelloWorldFirefoxWithHARTest {

    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;

    BrowserMobProxy proxy;

    @Before
    public void setup() {
        proxy = new BrowserMobProxyServer();
        proxy.start();
        proxy.newHar();
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT,
                CaptureType.RESPONSE_CONTENT);

        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
        FirefoxOptions options = new FirefoxOptions();
        options.setProxy(seleniumProxy);
        options.setAcceptInsecureCerts(true);

        driver = WebDriverManager.firefoxdriver()
                .capabilities(options).create();
        //WebDriverManager.firefoxdriver().useMirror().setup();
    }

    @After
    public void teardown() throws IOException {
        Har har = proxy.getHar();
        File harFile = new TestHelper(this.getClass()).resolveOutput("HelloWorldFirefox.har").toFile();
        har.writeTo(harFile);

        proxy.stop();
        driver.quit();
    }

    @Test
    public void test() {
        // Exercise
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        driver.get(sutUrl);
        String title = driver.getTitle();
        log.debug("The title of {} is {}", sutUrl, title);

        // Verify
        assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");
    }

}

