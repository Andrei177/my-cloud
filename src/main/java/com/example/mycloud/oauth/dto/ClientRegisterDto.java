package com.example.mycloud.oauth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClientRegisterDto {
    @NotEmpty(message = "clientId не может быть пустым")
    private String clientId;
    @NotEmpty(message = "clientSecret не может быть пустым")
    private String clientSecret;
    @NotEmpty(message = "clientName не может быть пустым")
    private String clientName;
    @NotEmpty(message = "redirectUri не может быть пустым")
    private String redirectUri;
}
