package com.thesecurex.portal.service;

import com.thesecurex.portal.model.InviteCode;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password, String inviteCodeStr) {
        // Validate the Invite Code using the service
        InviteCode invite = inviteCodeService.validateCode(inviteCodeStr);

        // Create the User
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(invite.getRoleToAssign()); // Set by OEM
        user.setAccessGroup(invite.getTargetGroup());

        // Burn the code using the service
        inviteCodeService.markAsUsed(inviteCodeStr);
        
        return userRepo.save(user);
    }
}
