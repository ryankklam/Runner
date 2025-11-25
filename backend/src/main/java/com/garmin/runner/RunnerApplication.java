package com.garmin.runner;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Garmin Runner应用主类
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Garmin运动数据API",
                version = "1.0.0",
                description = "提供运动活动管理、数据导入和统计分析功能",
                contact = @Contact(name = "Garmin Runner Team", email = "support@garminrunner.com"),
                license = @License(name = "Apache 2.0", url = "http://springdoc.org")
        )
)
public class RunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunnerApplication.class, args);
    }
}
