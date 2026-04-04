package com.example.mycloud.auth.dto;

import com.example.mycloud.entities.Genders;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(Long userId, LocalDate userBirthday, String userName, String userEmail, Genders userGender, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
