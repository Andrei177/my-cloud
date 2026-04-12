package com.example.mycloud.oauth.dto;

public record OAuthForm (String clientId, String redirectUri, String state, String userEmail, String password, String responseType) {
}
