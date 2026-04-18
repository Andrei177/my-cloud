package com.example.mycloud.oauth.dto;

import com.example.mycloud.oauth.entities.OAuthClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ClientRegisterResponseDto {
    private OAuthClient client;
    private String clientSecret;
}
