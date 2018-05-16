package com.ellin.demo.scg.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class RedirectToPathFromHostNameFilterFactory extends AbstractGatewayFilterFactory<RedirectToPathFromHostNameFilterFactory.Config> {


    public static final String URL_KEY = "url";
    public static final String REDIRECT_KEY="redirectCode";

    public RedirectToPathFromHostNameFilterFactory(){
        super(RedirectToPathFromHostNameFilterFactory.Config.class);
    }

    @Override
    public String name(){
        return "RedirectToPathFromHostName";
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(URL_KEY,REDIRECT_KEY);
    }

    public GatewayFilter apply(RedirectToPathFromHostNameFilterFactory.Config config) {

        String replacement = config.url;

        HttpStatus httpStatus = ServerWebExchangeUtils.parse(config.getRedirectCode());

        return this.apply(httpStatus,replacement);
    }



    public GatewayFilter apply(HttpStatus httpStatus, String replacement) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.defer(() -> {
            if (!exchange.getResponse().isCommitted()) {


                String host = exchange.getRequest().getURI().getHost();

                String newPath = String.format(replacement,host);

                ServerWebExchangeUtils.setResponseStatus(exchange, httpStatus);
                ServerHttpResponse response = exchange.getResponse();
                response.getHeaders().set("Location", newPath);
                return response.setComplete();
            } else {
                return Mono.empty();
            }
        }));
    }


    public static class Config {
        private String url;
        private String redirectCode;

        public String getRedirectCode() {
            return redirectCode;
        }

        public Config setRedirectCode(String redirectCode) {
            this.redirectCode = redirectCode;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Config setUrl(String url) {
            this.url = url;
            return this;
        }
    }
}

