package com.crediya.config;

import com.crediya.security.JwtProperties;
import com.crediya.security.JwtReactiveAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.crediya.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {
    // ===== JWT =====
    @Bean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(JwtProperties props) {
        return new JwtReactiveAuthenticationManager(props);
    }

}
