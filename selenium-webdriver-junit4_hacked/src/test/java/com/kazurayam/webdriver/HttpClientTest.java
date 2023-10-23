package com.kazurayam.webdriver;

import org.junit.Test;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpClient.Factory;
import org.openqa.selenium.remote.http.HttpClientName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Set;

public class HttpClientTest {

    static Logger log = LoggerFactory.getLogger(HttpClientTest.class);

    static Factory create(String name) {
        log.info("name : " + name);

        ServiceLoader<HttpClient.Factory> loader =
                ServiceLoader.load(HttpClient.Factory.class, HttpClient.Factory.class.getClassLoader());
        loader.forEach(p -> { log.info("hello"); });

        Set<Factory> factories =
                StreamSupport.stream(loader.spliterator(), true)
                        .filter(p -> p.getClass().isAnnotationPresent(HttpClientName.class))
                        .filter(p -> name.equals(p.getClass().getAnnotation(HttpClientName.class).value()))
                        .collect(Collectors.toSet());
        if (factories.isEmpty()) {
            throw new IllegalArgumentException("Unknown HttpClient factory " + name);
        }
        if (factories.size() > 1) {
            throw new IllegalStateException(
                    String.format(
                            "There are multiple HttpClient factories by name %s, check your classpath", name));
        }
        return factories.iterator().next();
    }

    @Test
    public void testCreateFactory() {
        Factory fac = create("jdk-http-client");
    }

}
