package com.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 * 用于支持 Android 应用访问后端 API
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/app/**")
                // 允许所有源（生产环境建议指定具体域名）
                .allowedOriginPatterns("*")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证（如 Cookie）
                .allowCredentials(true)
                // 预检请求的有效期（秒）
                .maxAge(3600);
    }

    /**
     * CORS 过滤器配置（备用方案）
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许所有源
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带凭证
        config.setAllowCredentials(true);
        // 预检请求有效期
        config.setMaxAge(3600L);
        
        // 对所有路径应用 CORS 配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

