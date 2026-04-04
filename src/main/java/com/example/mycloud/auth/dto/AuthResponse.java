package com.example.mycloud.auth.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private UserResponse user;
}
