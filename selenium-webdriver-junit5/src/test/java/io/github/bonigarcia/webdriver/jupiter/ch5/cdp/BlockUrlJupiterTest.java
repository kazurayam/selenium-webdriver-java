/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.webdriver.jupiter.ch5.cdp;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v92.network.Network;
import org.openqa.selenium.devtools.v92.network.model.BlockedReason;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;

import io.github.bonigarcia.wdm.WebDriverManager;

class BlockUrlJupiterTest {

    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;

    DevTools devTools;

    @BeforeEach
    void setup() {
        driver = WebDriverManager.chromedriver().create();
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();
    }

    @AfterEach
    void teardown() throws InterruptedException {
        // FIXME: pause for manual browser inspection
        Thread.sleep(Duration.ofSeconds(3).toMillis());

        devTools.close();
        driver.quit();
    }

    @Test
    void testBasicAuth() {
        devTools.send(Network.enable(Optional.empty(), Optional.empty(),
                Optional.empty()));

        String urlToBlock = "https://bonigarcia.dev/selenium-webdriver-java/img/hands-on-icon.png";
        devTools.send(Network.setBlockedURLs(ImmutableList.of(urlToBlock)));

        devTools.addListener(Network.loadingFailed(), loadingFailed -> {
            BlockedReason reason = loadingFailed.getBlockedReason().get();
            log.debug("Blocking reason: {}", reason);
            assertThat(reason).isEqualTo(BlockedReason.INSPECTOR);
        });

        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
    }
}
