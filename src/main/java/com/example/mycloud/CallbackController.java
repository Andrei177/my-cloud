package com.example.mycloud;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// КОНТРОЛЛЕР ДЛЯ ПРОВЕРКИ РЕДИРЕКТА ВМЕСТЕ С КОДОМ НА ВНЕШНЕЕ ПРИЛОЖЕНИЕ, ПОТОМ УДАЛИТЬ
@RestController
public class CallbackController {
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) {
        return "Внешнее приложение получило код " + code + " для обмена на токен";
    }
}
