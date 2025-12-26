package com.niit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问： http://localhost:8080(或/niit)/upload/xxx.jpg
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:D:/niit_uploads/");
    }
}
