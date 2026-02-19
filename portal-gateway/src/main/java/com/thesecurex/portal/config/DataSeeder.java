package com.thesecurex.portal.config;

import com.thesecurex.portal.model.Role;
import com.thesecurex.portal.model.Tool;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.model.AccessGroup;
import com.thesecurex.portal.repository.AccessGroupRepository;
import com.thesecurex.portal.repository.ToolRepository;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, ToolRepository toolRepo, AccessGroupRepository accessGroupRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin User
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.OEM);
                userRepo.save(admin);
                System.out.println("✅ Default Admin User Created: admin / admin123");
            }

            // Seed Tools
            List<Tool> seededTools = new ArrayList<>();
            List<Tool> defaultTools = List.of(
                createTool("TheLogX", "TheSecureAnalyzerX (AI Engine)", "fa-file-code", "http://localhost:8000/"),
                createTool("TheNetProtectX", "TheNetProtectX (Network Sniffer)", "fa-shield-alt", "http://localhost:8002/"),
                createTool("TheOsIntX", "Open Source Intelligence Tool", "fa-user-secret", "http://localhost:8000/osint"),
                createTool("TheCrackX", "Password Strength Tester", "fa-key", "http://localhost:8000/crackx")
            );

            for (Tool tool : defaultTools) {
                if (toolRepo.findByName(tool.getName()).isEmpty()) {
                    seededTools.add(toolRepo.save(tool));
                    System.out.println("✅ Tool Created: " + tool.getName());
                } else {
                    Tool existingTool = toolRepo.findByName(tool.getName()).get();
                    existingTool.setEndpoint(tool.getEndpoint());
                    seededTools.add(toolRepo.save(existingTool));
                    System.out.println("✅ Tool Updated: " + tool.getName());
                }
            }
            
            // Seed Test User with Tools
            if (userRepo.findByUsername("user").isEmpty()) {
                // Create Access Group
                AccessGroup accessGroup = new AccessGroup();
                accessGroup.setName("Default Group");
                accessGroup.setAllowedTools(seededTools);
                accessGroupRepo.save(accessGroup);
                
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.USER);
                user.setAccessGroup(accessGroup);
                userRepo.save(user);
                System.out.println("✅ Default User Created: user / user123");
            }
        };
    }

    private Tool createTool(String name, String description, String icon, String endpoint) {
        Tool tool = new Tool();
        tool.setName(name);
        tool.setDescription(description);
        tool.setIcon(icon);
        tool.setEndpoint(endpoint);
        return tool;
    }
}
