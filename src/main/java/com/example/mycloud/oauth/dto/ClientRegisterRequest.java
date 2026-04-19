package com.example.mycloud.oauth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClientRegisterRequest {
    @NotEmpty(message = "clientName не может быть пустым")
    private String clientName;

    @NotEmpty(message = "redirectUri не может быть пустым")
    private String redirectUri;
}
