package com.higame.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HiGame SDK API文档")
                        .version("1.0.0")
                        .description("HiGame SDK服务端API接口文档，包含用户账号系统所有接口")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                            .name("HiGame Team")
                            .email("support@higame.com")
                            .url("https://www.higame.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("使用JWT token进行身份验证")))
                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                        .description("HiGame SDK Wiki")
                        .url("https://wiki.higame.com"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("higame-public")
                .pathsToMatch("/api/**")
                .packagesToScan("com.higame.account.controller", "com.higame.core.controller")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("higame-admin")
                .pathsToMatch("/admin/**")
                .packagesToScan("com.higame.admin.controller", "com.higame.management.controller")
                .build();
    }
}