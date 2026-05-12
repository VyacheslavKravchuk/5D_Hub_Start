package com.example.gateway_routes;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
@Configuration
public class GatewayConfiguration {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user_route_users", r -> r.path("/users/**")
                        .uri("lb://d-hub-user-service"))
                .route("user_route_companies", r -> r.path("/users_companies/**")
                        .uri("lb://d-hub-user-service"))
                .route("company_route_companies", r -> r.path("/companies/**")
                        .uri("lb://d-hub-company-service"))
                .route("company_route_proxy", r -> r.path("/proxy/companies/**")
                        .uri("lb://d-hub-company-service"))
                .build();
    }

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

