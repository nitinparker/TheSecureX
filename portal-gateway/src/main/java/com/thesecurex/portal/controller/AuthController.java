package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.User;
import com.thesecurex.portal.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String inviteCode = payload.get("inviteCode");

        try {
            User user = authService.registerUser(username, password, inviteCode);
            return ResponseEntity.ok(Map.of("message", "User created successfully", "userId", user.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public String health() {
        return "Portal Gateway (Java) is running!";
    }
}
