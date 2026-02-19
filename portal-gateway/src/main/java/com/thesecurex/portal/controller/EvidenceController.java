package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.Evidence;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.UserRepository;
import com.thesecurex.portal.service.EvidenceVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/vault")
public class EvidenceController {

    @Autowired
    private EvidenceVaultService vaultService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadEvidence(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam(value = "username", required = false) String username, // For tools that send username
            Authentication authentication) {
        
        try {
            User user = null;
            if (authentication != null && authentication.isAuthenticated()) {
                String currentUsername = authentication.getName();
                Optional<User> userOpt = userRepository.findByUsername(currentUsername);
                if (userOpt.isPresent()) user = userOpt.get();
            }
            
            // Fallback for tools sending username explicitly if auth is token-based but not fully integrated yet
            if (user == null && username != null) {
                Optional<User> userOpt = userRepository.findByUsername(username);
                if (userOpt.isPresent()) user = userOpt.get();
            }

            if (user == null) {
                return ResponseEntity.status(401).body("User authentication required");
            }

            Evidence evidence = vaultService.store(file, user, type);
            return ResponseEntity.ok("Evidence Stored and Hashed successfully. ID: " + evidence.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Vault Error: " + e.getMessage());
        }
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verifyEvidence(@PathVariable Long id) {
        try {
            boolean intact = vaultService.verifyIntegrity(id);
            if (intact) {
                return ResponseEntity.ok("Integrity Verified: SHA-256 Match.");
            } else {
                return ResponseEntity.status(409).body("TAMPERED: Hash Mismatch!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Verification Error: " + e.getMessage());
        }
    }
}
