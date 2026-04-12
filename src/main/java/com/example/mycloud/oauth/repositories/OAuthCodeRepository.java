package com.example.mycloud.oauth.repositories;

import com.example.mycloud.oauth.entities.OAuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthCodeRepository extends JpaRepository<OAuthCode, String> {
}
