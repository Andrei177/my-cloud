package com.example.mycloud.auth.dto;

import com.example.mycloud.entities.Genders;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignupRequest {
    @NotBlank(message = "Имя обязательно")
    private String userName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неправильный email формат")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен состоять минимум из 6 символов")
    private String password;

    private LocalDate userBirthday;
    private Genders userGender;
}