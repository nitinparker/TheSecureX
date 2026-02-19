package com.thesecurex.portal.service;

import com.thesecurex.portal.model.AccessGroup;
import com.thesecurex.portal.model.Tool;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public long countAll() {
        return userRepo.count();
    }

    @Transactional(readOnly = true)
    public List<Tool> getToolsForUser(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getAccessGroup() != null) {
            // Force initialization of lazy collection if needed, though AccessGroup is likely EAGER
            // AccessGroup.allowedTools is ManyToMany (LAZY), so we need Transactional
            user.getAccessGroup().getAllowedTools().size(); // touch to load
            return user.getAccessGroup().getAllowedTools();
        }
        return Collections.emptyList();
    }
}
