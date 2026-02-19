package com.thesecurex.portal.service;

import com.thesecurex.portal.model.InviteCode;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.InviteCodeRepository;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private InviteCodeRepository inviteRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password, String inviteCode) {
        // Validate the Invite Code
        InviteCode invite = inviteRepo.findByCode(inviteCode)
            .orElseThrow(() -> new RuntimeException("Invalid Code"));

        if (invite.isUsed() || invite.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code expired or already used");
        }

        // Create the User
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(invite.getRoleToAssign()); // Set by OEM
        user.setAccessGroup(invite.getTargetGroup());

        invite.setUsed(true); // Burn the code
        inviteRepo.save(invite);
        
        return userRepo.save(user);
    }
}
