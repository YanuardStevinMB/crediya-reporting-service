package com.crediya.api.config;

import com.crediya.security.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(JwtProperties.class)


public class SecurityConfig {
    private final BearerServerSecurityContextRepository contextRepo;
    public SecurityConfig(BearerServerSecurityContextRepository contextRepo) {
        this.contextRepo = contextRepo;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(contextRepo)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Swagger completo abierto
                        .pathMatchers("/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/reports").permitAll()


                        .anyExchange().authenticated()
                )
                //  evita que Spring intente “/login”
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((exchange, ex) ->
                                Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED)))
                        .accessDeniedHandler((exchange, ex) ->
                                Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN)))
                )
                .build();
    }
}
