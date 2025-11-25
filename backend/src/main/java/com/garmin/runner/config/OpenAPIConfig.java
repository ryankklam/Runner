package com.garmin.runner.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger OpenAPI配置类，用于配置API文档
 */
@Configuration
public class OpenAPIConfig {

    /**
     * 配置OpenAPI文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Garmin运动数据API文档")
                        .version("1.0.0")
                        .description("Garmin运动数据导入与统计分析应用的API文档，提供活动管理、数据导入和统计分析功能")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
