package com.example.mycloud.auth;

import com.example.mycloud.auth.dto.SigninRequest;
import com.example.mycloud.auth.dto.SignupRequest;
import com.example.mycloud.auth.dto.AuthResponse;
import com.example.mycloud.utils.OperationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Регистрация",
            description = "Пользователь передаёт данные для регистрации",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.AUTH)
            }),
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Успешная регистрация"),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
    })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthResponse response = authService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Вход",
            description = "Пользователь передаёт данные для входа в аккаунт облачного хранилища",
            extensions = @Extension(properties = {
                    @ExtensionProperty(name = "x-operation-group", value = OperationGroups.AUTH)
            }),
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "404", description = "Пользователя не существет", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный пароль", content = @Content(mediaType = "application/json", schema = @Schema(contentSchema = ErrorResponse.class)))
    })
    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        AuthResponse response = authService.signin(signinRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
