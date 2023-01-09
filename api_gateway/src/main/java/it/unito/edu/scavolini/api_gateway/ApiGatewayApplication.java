package it.unito.edu.scavolini.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ApiGatewayApplication {

    @Value("${reservation_microservice_url}")
    private String reservation_microservice_url;

    @Value("${order_microservice_url}")
    private String order_microservice_url;

    @Value("${waiter_microservice_url}")
    private String waiter_microservice_url;

    @Value("${kitchen_microservice_url}")
    private String kitchen_microservice_url;

    @Value("${menu_microservice_url}")
    private String menu_microservice_url;

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(p -> p
                .path("/waiter/**")
                .filters(f -> f.rewritePath("/waiter/(?<segment>.*)", "/${segment}"))
                .uri(waiter_microservice_url))
            .route(p -> p
                .path("/kitchen/**")
                .filters(f -> f.rewritePath("/kitchen/(?<segment>.*)", "/${segment}"))
                .uri(kitchen_microservice_url))
            .route(p -> p
                .path("/menu/**")
                .filters(f -> f.rewritePath("/menu/(?<segment>.*)", "/${segment}"))
                .uri(menu_microservice_url))
            .route(p -> p
                .path("/reservation/**")
                .filters(f -> f.rewritePath("/reservation/(?<segment>.*)", "/${segment}"))
                .uri(reservation_microservice_url))
            .route(p -> p
                .path("/order/**")
                .filters(f -> f.rewritePath("/order/(?<segment>.*)", "/${segment}"))
                .uri(order_microservice_url))
            .build();
    }

}
