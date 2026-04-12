package com.example.mycloud.oauth.entities;

import com.example.mycloud.oauth.types.OAuthClientStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_clients")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OAuthClient {
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "redirect_uri")
    private String redirectUri;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OAuthClientStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
