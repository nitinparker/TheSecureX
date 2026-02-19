package com.thesecurex.portal.controller;

import com.thesecurex.portal.repository.TrialRequestRepository;
import com.thesecurex.portal.service.InviteCodeService;
import com.thesecurex.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private TrialRequestRepository trialRequestRepository;

    @GetMapping("/dashboard")
    public String dynamicDashboard(Authentication authentication, Model model) {
        String role = authentication.getAuthorities().toString();
        
        if (role.contains("ROLE_OEM")) {
            // Add OEM specific analytics 
            model.addAttribute("totalUsers", userService.countAll());
            model.addAttribute("systemHealth", "Optimal");
            model.addAttribute("pendingInvitesCount", inviteCodeService.countPendingInvites());
            model.addAttribute("pendingRequests", trialRequestRepository.findByProcessedFalse().size());
            return "admin/dashboard"; 
        } else if (role.contains("ROLE_MASTER")) { 
            return "redirect:/master/dashboard"; 
        } else { 
            // Standard User Launchpad 
            model.addAttribute("myTools", userService.getToolsForUser(authentication.getName())); 
            return "user/launchpad"; 
        } 
    }
}
