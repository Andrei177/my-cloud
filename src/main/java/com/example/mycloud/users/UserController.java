package com.example.mycloud.users;

import com.example.mycloud.auth.dto.UserResponse;
import com.example.mycloud.exceptions.UserByEmailNotFound;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserRepository userRepository;
    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/greeting")
    public ResponseEntity<String> getGreeting(){
        return ResponseEntity.status(200).body("Hello World!!!");
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails user){
        User userInfo = userRepository.findByUserEmail(user.getUsername()).orElseThrow(() -> new UserByEmailNotFound(user.getUsername()));
        UserResponse userResponse = new UserResponse(userInfo.getUserId(), userInfo.getUserBirthday(), userInfo.getUserName(), userInfo.getUserEmail(), userInfo.getUserGender(), userInfo.getCreatedAt(), userInfo.getUpdatedAt());
        return ResponseEntity.status(200).body(userResponse);
    }
}
