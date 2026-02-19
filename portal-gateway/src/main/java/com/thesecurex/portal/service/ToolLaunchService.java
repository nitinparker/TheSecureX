package com.thesecurex.portal.service;

import com.thesecurex.portal.entity.LaunchToken;
import com.thesecurex.portal.repository.LaunchTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class ToolLaunchService {

    @Autowired
    private LaunchTokenRepository launchTokenRepository;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public LaunchToken generateToken(String username, Long toolId) {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        String token = base64Encoder.encodeToString(randomBytes);

        LaunchToken launchToken = new LaunchToken(
                token,
                username,
                toolId,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(60) // Token valid for 60 seconds
        );

        return launchTokenRepository.save(launchToken);
    }

    @Transactional
    public Optional<LaunchToken> verifyAndBurnToken(String token) {
        Optional<LaunchToken> launchTokenOpt = launchTokenRepository.findByToken(token);

        if (launchTokenOpt.isPresent()) {
            LaunchToken launchToken = launchTokenOpt.get();
            
            // Check expiry
            if (launchToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                launchTokenRepository.delete(launchToken); // Cleanup expired
                return Optional.empty();
            }

            // Burn token (one-time use)
            launchTokenRepository.delete(launchToken);
            return Optional.of(launchToken);
        }

        return Optional.empty();
    }
}
