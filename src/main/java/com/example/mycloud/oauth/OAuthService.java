package com.example.mycloud.oauth;

import com.example.mycloud.exceptions.*;
import com.example.mycloud.oauth.dto.ClientRegisterDto;
import com.example.mycloud.oauth.dto.OAuthForm;
import com.example.mycloud.oauth.dto.TokenRequest;
import com.example.mycloud.oauth.entities.OAuthClient;
import com.example.mycloud.oauth.entities.OAuthCode;
import com.example.mycloud.oauth.repositories.OAuthClientRepository;
import com.example.mycloud.oauth.repositories.OAuthCodeRepository;
import com.example.mycloud.oauth.types.OAuthClientStatus;
import com.example.mycloud.security.JwtService;
import com.example.mycloud.users.User;
import com.example.mycloud.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OAuthService {
    private final OAuthClientRepository oAuthClientRepository;
    private final OAuthCodeRepository oAuthCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public OAuthService(OAuthClientRepository oAuthClientRepository, OAuthCodeRepository oAuthCodeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.oAuthClientRepository = oAuthClientRepository;
        this.oAuthCodeRepository = oAuthCodeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public OAuthClient checkClient(String clientId, String redirectUri) {
        OAuthClient clientApp = oAuthClientRepository.findById(clientId).orElseThrow(() -> new ClientAppNotFound("Приложения " +  clientId + " нет в базе данных клиентов"));

        if(!clientApp.getRedirectUri().equals(redirectUri)){
            throw new OAuthException("переданный URL для редиректа не совпадает с URL в БД");
        };
        if(clientApp.getStatus().equals(OAuthClientStatus.BLOCKED)){
            throw new OAuthException("Приложение заблокировано");
        }
        return clientApp;
    }
    public OAuthCode getCode(OAuthForm form) {
        if (form.responseType() == null || !"code".equals(form.responseType())) {
            throw new OAuthException("Некорректный response_type");
        }
        OAuthClient clientApp = this.checkClient(form.clientId(), form.redirectUri());
        User user = userRepository.findByUserEmail(form.userEmail()).orElseThrow(() ->  new UserByEmailNotFound(form.userEmail()));

        if(!passwordEncoder.matches(form.password(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        OAuthCode newCode = new OAuthCode(UUID.randomUUID().toString(), user, clientApp, LocalDateTime.now().plusMinutes(10), LocalDateTime.now());

        return oAuthCodeRepository.save(newCode);
    }

    public OAuthClient registerClient(ClientRegisterDto clientData) {
        OAuthClient client = new OAuthClient(clientData.getClientId(), passwordEncoder.encode(clientData.getClientSecret()), clientData.getClientName(), clientData.getRedirectUri(), OAuthClientStatus.ACTIVE, LocalDateTime.now());
        oAuthClientRepository.save(client);
        return client;
    }

    public String exchangeCodeForToken(TokenRequest tokenRequest){
        if(!tokenRequest.getGrantType().equals("authorization_code")){
            throw new OAuthException("Неверный grant_type");
        }
        OAuthClient clientApp = this.checkClient(tokenRequest.getClientId(), tokenRequest.getRedirectUri());
        if(!passwordEncoder.matches(tokenRequest.getClientSecret(), clientApp.getClientSecret())){
            throw new OAuthException("Неверный пароль");
        }
        OAuthCode code = oAuthCodeRepository.findById(tokenRequest.getCode()).orElseThrow(() -> new OAuthException("Кода не существует"));

        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OAuthException("Срок действия кода истек");
        }

        oAuthCodeRepository.delete(code);

        User user = code.getUser();
        if(user == null){
            throw new UserNotFound("Пользователь для которого был сгенерирова код не найден");
        }

        return jwtService.generateToken(code.getUser());
    }
}
