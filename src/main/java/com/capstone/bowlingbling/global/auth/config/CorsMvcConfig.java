package com.capstone.bowlingbling.global.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {

        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedOrigins("https://bowlingbling.duckdns.org")
                .allowedOrigins("https://bowlingbling.duckdns.org:8080/login/oauth2/code/google")
                .allowedOrigins("https://bowlingbling.duckdns.org:8080/login/oauth2/code/kakao")
                .allowedOrigins("https://bowlingbling.duckdns.org:8081/login/oauth2/code/google")
                .allowedOrigins("https://bowlingbling.duckdns.org:8081/login/oauth2/code/kakao");
    }
}