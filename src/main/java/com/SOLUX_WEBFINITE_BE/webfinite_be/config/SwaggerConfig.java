package com.SOLUX_WEBFINITE_BE.webfinite_be.config;

//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 로그아웃 위한 추가
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
// 로그아웃 위한 추가

@Configuration    // 스프링 실행시 설정파일 읽어드리기 위한 어노테이션
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // SecurityRequirement 객체 생성
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
//                .components(new Components()) // 기존 설정
//                .info(apiInfo()); // 기존 설정
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))); // JWT 토큰 설정 추가
    }

    private Info apiInfo() {
        return new Info()
                .title("Webfinite Swagger")
                .description("AI학습관리 서비스를 위한 APi")
                .version("1.0.0");
    }
}
