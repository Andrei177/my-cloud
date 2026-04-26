package com.example.mycloud.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            writeUnauthorized(response, "Отсутствует Bearer токен");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "Некорректный формат Authorization header. Ожидался Bearer токен");
            return;
        }

        String jwt = authHeader.substring(7).trim();

        if (jwt.isBlank()) {
            writeUnauthorized(response, "Bearer токен пустой");
            return;
        }

        if (!jwtService.validateToken(jwt)) {
            writeUnauthorized(response, "Токен невалиден или истёк");
            return;
        }

        String userEmail = jwtService.extractUsername(jwt);
        Long userId = jwtService.extractUserId(jwt);

        if (userEmail == null || userId == null) {
            writeUnauthorized(response, "Не удалось получить данные пользователя из токена");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails userDetails = new CustomUserDetails(
                    userEmail,
                    "",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                    userId
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        String body = """
                {"message":"%s"}
                """.formatted(escape(message));

        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/api/v1/oauth/authorize")
                || path.startsWith("/api/v1/oauth/token")
                || path.startsWith("/api/v1/oauth/clients")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/api/v1/public/")
                || path.startsWith("/callback");
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