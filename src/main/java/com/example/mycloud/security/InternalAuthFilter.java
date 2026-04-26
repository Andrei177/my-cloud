package com.example.mycloud.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String INTERNAL_AUTH_HEADER = "X-Internal-Auth";

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/api/v1/auth/",
            "/api/v1/oauth/authorize",
            "/api/v1/oauth/token",
            "/v3/api-docs",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/api/v1/public/",
            "/callback"
    );

    private final String internalAuthToken;

    public InternalAuthFilter(String internalAuthToken) {
        this.internalAuthToken = internalAuthToken;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String actualToken = request.getHeader(INTERNAL_AUTH_HEADER);

        if (!isValidInternalToken(actualToken)) {
            writeForbidden(response, "Отсутствует внутренний токен доступа");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private boolean isValidInternalToken(String actualToken) {
        if (actualToken == null || actualToken.isBlank()) {
            return false;
        }

        byte[] expectedBytes = internalAuthToken.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actualToken.getBytes(StandardCharsets.UTF_8);

        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private void writeForbidden(
            HttpServletResponse response,
            String message
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        String body = """
                {"message":"%s"}
                """.formatted(escape(message));

        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}