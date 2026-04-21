package com.example.mycloud.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiSecurityCustomizer {

    @Bean
    public OpenApiCustomizer publicEndpointsSecurityCustomizer() {
        return openApi -> {
            setNoSecurity(openApi, "/api/v1/auth/signin", PathItem.HttpMethod.POST);
            setNoSecurity(openApi, "/api/v1/auth/signup", PathItem.HttpMethod.POST);
            setNoSecurity(openApi, "/api/v1/oauth/authorize", PathItem.HttpMethod.POST);
            setNoSecurity(openApi, "/api/v1/oauth/authorize", PathItem.HttpMethod.GET);
            setNoSecurity(openApi, "/api/v1/oauth/token", PathItem.HttpMethod.POST);
        };
    }

    private void setNoSecurity(OpenAPI openApi, String path, PathItem.HttpMethod httpMethod) {
        if (openApi.getPaths() == null || openApi.getPaths().get(path) == null) {
            return;
        }

        PathItem pathItem = openApi.getPaths().get(path);
        Operation operation = pathItem.readOperationsMap().get(httpMethod);

        if (operation != null) {
            operation.setSecurity(Collections.emptyList());
        }
    }
}