package com.thesecurex.portal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // Check if Google Client ID is configured and not the default placeholder
        boolean googleAuthEnabled = googleClientId != null && !googleClientId.isEmpty() && !"your-client-id".equals(googleClientId);
        model.addAttribute("googleAuthEnabled", googleAuthEnabled);
        return "login";
    }

}
