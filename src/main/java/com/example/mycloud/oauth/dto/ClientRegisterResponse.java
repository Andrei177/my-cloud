package com.example.mycloud.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClientRegisterResponse {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
