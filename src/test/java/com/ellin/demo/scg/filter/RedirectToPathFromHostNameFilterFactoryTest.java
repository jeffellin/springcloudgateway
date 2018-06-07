package com.ellin.demo.scg.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RedirectToPathFromHostNameFilterFactoryTest {


    @LocalServerPort
    protected int port = 0;

    protected WebTestClient testClient;
    protected WebClient webClient;
    protected String baseUri;
    
    @Before
    public void setup() {
        baseUri = "http://localhost:" + port;
        this.webClient = WebClient.create(baseUri);
        this.testClient = WebTestClient.bindToServer().baseUrl(baseUri).build();
    }

    @Test
    public void setRequestHeaderFilterWorks() {
        testClient.get().uri("/")
                .header("Host", "ellin.com")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION,"/website/ellin.com/home");
    }
}