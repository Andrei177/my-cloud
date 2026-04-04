package com.example.mycloud.auth;

import com.example.mycloud.auth.dto.SigninRequest;
import com.example.mycloud.auth.dto.SignupRequest;
import com.example.mycloud.auth.dto.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthResponse response = authService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        AuthResponse response = authService.signin(signinRequest);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
