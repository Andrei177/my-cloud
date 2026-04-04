package com.example.mycloud.auth;

import com.example.mycloud.auth.dto.SigninRequest;
import com.example.mycloud.auth.dto.SignupRequest;
import com.example.mycloud.auth.dto.AuthResponse;
import com.example.mycloud.auth.dto.UserResponse;
import com.example.mycloud.users.User;
import com.example.mycloud.exceptions.UserAlreadyExists;
import com.example.mycloud.exceptions.UserByEmailNotFound;
import com.example.mycloud.exceptions.WrongPasswordException;
import com.example.mycloud.security.JwtService;
import com.example.mycloud.users.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private JwtService jwtService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        Optional<User> candidate = userRepository.findByUserEmail(signupRequest.getEmail());

        if (candidate.isPresent()) {
            throw new UserAlreadyExists(candidate.get().getUserEmail());
        }
        User newUser = new User();
        newUser.setUserEmail(signupRequest.getEmail());
        newUser.setUserBirthday(signupRequest.getUserBirthday());
        newUser.setUserGender(signupRequest.getUserGender());
        newUser.setUserName(signupRequest.getUserName());
        newUser.setUserPassword(passwordEncoder.encode(signupRequest.getPassword()));
        User savedUser = userRepository.save(newUser);

        String accessToken = jwtService.generateToken(savedUser);

        AuthResponse response = new AuthResponse();
        response.setUser(new UserResponse(savedUser.getUserId(), savedUser.getUserBirthday(), savedUser.getUserName(), savedUser.getUserEmail(), savedUser.getUserGender(), savedUser.getCreatedAt(), savedUser.getUpdatedAt()));
        response.setAccessToken(accessToken);

        return response;
    }

    public AuthResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByUserEmail(signinRequest.getEmail()).orElseThrow(() ->  new UserByEmailNotFound(signinRequest.getEmail()));

        if(!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        String accessToken = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse();
        response.setUser(new UserResponse(user.getUserId(), user.getUserBirthday(), user.getUserName(), user.getUserEmail(), user.getUserGender(), user.getCreatedAt(), user.getUpdatedAt()));
        response.setAccessToken(accessToken);

        return response;
    }
}
