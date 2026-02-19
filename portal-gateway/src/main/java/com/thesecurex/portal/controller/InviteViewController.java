package com.thesecurex.portal.controller;

import com.thesecurex.portal.repository.InviteCodeRepository;
import com.thesecurex.portal.service.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/invites")
@PreAuthorize("hasRole('OEM')")
public class InviteViewController {

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @GetMapping
    public String showInviteDashboard(Model model) {
        // Fetch all codes to display in the table
        model.addAttribute("invites", inviteCodeRepository.findAll());
        return "admin/invites"; // Maps to templates/admin/invites.html
    }

    @PostMapping("/generate-ui")
    public String generateViaUI() {
        inviteCodeService.createInviteCode("MASTER", 7);
        return "redirect:/admin/invites?success";
    }
}
