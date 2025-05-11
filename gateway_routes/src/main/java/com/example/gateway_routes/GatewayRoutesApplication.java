package com.example.gateway_routes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayRoutesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayRoutesApplication.class, args);
	}

}
