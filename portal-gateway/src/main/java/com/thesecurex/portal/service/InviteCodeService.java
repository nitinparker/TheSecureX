package com.thesecurex.portal.service;

import com.thesecurex.portal.model.InviteCode;
import com.thesecurex.portal.model.Role;
import com.thesecurex.portal.repository.InviteCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InviteCodeService {

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    /**
     * Generates a new secure invite code for the OEM to share.
     */
    public String createInviteCode(String roleName, int daysValid) {
        String generatedCode = "SX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        InviteCode invite = new InviteCode();
        invite.setCode(generatedCode);
        try {
            invite.setRoleToAssign(Role.valueOf(roleName));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Role: " + roleName);
        }
        invite.setExpiresAt(LocalDateTime.now().plusDays(daysValid));
        invite.setUsed(false);

        inviteCodeRepository.save(invite);
        return generatedCode;
    }

    /**
     * Generates a new secure invite code with specific Access Group and validity.
     */
    public String createInviteCodeWithGroup(String roleName, int daysValid, com.thesecurex.portal.model.AccessGroup group) {
        String generatedCode = "SX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        InviteCode invite = new InviteCode();
        invite.setCode(generatedCode);
        try {
            invite.setRoleToAssign(Role.valueOf(roleName));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Role: " + roleName);
        }
        invite.setExpiresAt(LocalDateTime.now().plusDays(daysValid));
        invite.setTargetGroup(group);
        invite.setUsed(false);

        inviteCodeRepository.save(invite);
        return generatedCode;
    }

    /**
     * Validates if a code is usable for registration.
     */
    public InviteCode validateCode(String code) {
        InviteCode invite = inviteCodeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid invitation code."));

        if (invite.isUsed()) {
            throw new RuntimeException("This code has already been used.");
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This code has expired.");
        }

        return invite;
    }

    /**
     * Marks a code as used after successful registration.
     */
    public void markAsUsed(String code) {
        InviteCode invite = inviteCodeRepository.findByCode(code).orElse(null);
        if (invite != null) {
            invite.setUsed(true);
            inviteCodeRepository.save(invite);
        }
    }

    public long countPendingInvites() {
        return inviteCodeRepository.countByUsedFalseAndExpiresAtAfter(LocalDateTime.now());
    }
}
