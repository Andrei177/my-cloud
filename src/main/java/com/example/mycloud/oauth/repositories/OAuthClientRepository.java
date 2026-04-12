package com.example.mycloud.oauth.repositories;

import com.example.mycloud.oauth.entities.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

}
