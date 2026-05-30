package com.ajay.lovable.commonlib.security;

import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AutoConfiguration
public class SharedSecurityAutoConfiguration {

    @Bean
    public AuthUtil authUtil() {
        return new AuthUtil();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(AuthUtil authUtil, HandlerExceptionResolver handlerExceptionResolver) {
        return new JwtAuthFilter(authUtil, handlerExceptionResolver);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("AUTH ajay = " + authentication);
            Object creds = authentication.getCredentials();
            if (creds instanceof String token) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
