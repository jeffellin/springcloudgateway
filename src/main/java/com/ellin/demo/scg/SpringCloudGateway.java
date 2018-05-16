package com.ellin.demo.scg;

import com.ellin.demo.scg.filter.RewritePathFromHostNameFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCloudGateway {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudGateway.class, args);
	}

	@Bean
	RewritePathFromHostNameFilterFactory demoGw() {
		return new RewritePathFromHostNameFilterFactory();
	}
}
