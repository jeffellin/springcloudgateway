package com.ellin.demo.scg.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
@TestPropertySource(properties = {"pathURI=http://localhost:9999/httpbin/anything","server.port=9999"})
public class RedirectToPathFromHostNameFilterFactoryTest {

	@Autowired
	private RouteDefinitionLocator routeDefinitionLocator;

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
	    routeDefinitionLocator.getRouteDefinitions().toStream().forEach(x->setURI(x));
    }

	private  void setURI(RouteDefinition x) {
		try {
			x.setUri(new URI("http://localhost:" + port+"/httpbin/anything"));
		} catch (URISyntaxException e) {
			Exceptions.propagate(e);
		}
	}

    @Test
    public void setRequestHeaderFilterWorks() {
        testClient.get().uri("/")
                .header("Host", "ellin.com")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION,"/website/ellin.com/home");
    }


    @Test
    public void mediaLinksWork() {
        testClient.get().uri("/media")
                .exchange()
                .expectBody(Map.class)
                .consumeWith(result -> {
                    Map<String, Object> headers = getMap(result.getResponseBody(), "headers");
                    assertThat(headers).containsEntry("X-Request-Example", "ValueA");
                });
    }

    public static Map<String, Object> getMap(Map response, String key) {

        return (Map<String, Object>) response.get(key);
    }
}