package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.InviteCode;
import com.thesecurex.portal.model.Role;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.InviteCodeRepository;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class SignupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(
            @RequestParam String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String inviteCode,
            @RequestParam String countryCode,
            @RequestParam String phoneNumber,
            @RequestParam(defaultValue = "false") boolean termsAccepted,
            Model model) {

        // Trim inputs
        firstName = firstName.trim();
        if (middleName != null) middleName = middleName.trim();
        lastName = lastName.trim();
        email = email.trim();
        username = username.trim();
        inviteCode = inviteCode.trim();
        countryCode = countryCode.trim();
        phoneNumber = phoneNumber.trim();

        if (!termsAccepted) {
            model.addAttribute("error", "You must accept the Terms and Conditions.");
            return "signup";
        }

        // Validate Invite Code
        Optional<InviteCode> codeOpt = inviteCodeRepository.findByCode(inviteCode);
        if (codeOpt.isEmpty()) {
            model.addAttribute("error", "Invalid Invitation Code.");
            return "signup";
        }

        InviteCode code = codeOpt.get();
        if (code.isUsed() || (code.getExpiresAt() != null && code.getExpiresAt().isBefore(LocalDateTime.now()))) {
            model.addAttribute("error", "Invitation Code is used or expired.");
            return "signup";
        }

        // Check if user exists
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already taken.");
            return "signup";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            model.addAttribute("error", "Email already registered.");
            return "signup";
        }

        // Create User
        User user = new User();
        user.setFirstName(firstName);
        user.setMiddleName(middleName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setCountryCode(countryCode);
        user.setPhoneNumber(phoneNumber);
        
        // Assign Role based on Invite Code
        user.setRole(code.getRoleToAssign());
        user.setAccessGroup(code.getTargetGroup());

        userRepository.save(user);

        // Burn Invite Code
        code.setUsed(true);
        inviteCodeRepository.save(code);

        return "redirect:/login?signupSuccess";
    }
}
