package com.thesecurex.portal.controller;

import java.util.Map;

import com.thesecurex.portal.service.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/invites")
@PreAuthorize("hasRole('OEM')") // Only the Super Admin can call this
public class InviteController {

    @Autowired
    private InviteCodeService inviteCodeService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateMasterCode() {
        // Generates a code for a new "Master" role valid for 7 days
        String code = inviteCodeService.createInviteCode("MASTER", 7);
        return ResponseEntity.ok(Map.of("code", code, "message", "New Master Invite Code Created"));
    }
}
