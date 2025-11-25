package com.garmin.runner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS配置类，用于处理跨域请求
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        
        // 设置允许的源，支持通配符
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // Spring Boot 2.4+ 推荐使用addAllowedOriginPattern
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // 允许客户端访问的响应头
        config.addExposedHeader("Content-Disposition");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("X-Requested-With");
        config.addExposedHeader("Authorization");
        
        // 设置预检请求的有效期，减少请求次数
        config.setMaxAge(3600L);
        
        // 创建URL映射源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}