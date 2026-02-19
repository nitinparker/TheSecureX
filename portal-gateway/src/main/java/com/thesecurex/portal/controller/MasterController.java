package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.AccessGroup;
import com.thesecurex.portal.model.Tool;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.AccessGroupRepository;
import com.thesecurex.portal.repository.ToolRepository;
import com.thesecurex.portal.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/master")
@PreAuthorize("hasRole('MASTER')")
public class MasterController {

    private final AccessGroupRepository groupRepo;
    private final ToolRepository toolRepo;
    private final UserRepository userRepo;

    public MasterController(AccessGroupRepository groupRepo, ToolRepository toolRepo, UserRepository userRepo) {
        this.groupRepo = groupRepo;
        this.toolRepo = toolRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        List<AccessGroup> groups = groupRepo.findByMasterUsername(username);
        List<Tool> allTools = toolRepo.findAll();

        model.addAttribute("groups", groups);
        model.addAttribute("availableTools", allTools);

        return "master/dashboard";
    }

    @PostMapping("/dashboard/create-group")
    public String createGroup(@RequestParam String groupName, @RequestParam(required = false) List<Long> toolIds) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User master = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Master user not found"));

        AccessGroup group = new AccessGroup();
        group.setName(groupName);
        group.setMaster(master);
        
        if (toolIds != null) {
            List<Tool> selectedTools = toolRepo.findAllById(toolIds);
            group.setAllowedTools(selectedTools);
        }

        groupRepo.save(group);
        return "redirect:/master/dashboard";
    }

    @PostMapping("/groups/{id}/delete")
    public String deleteGroup(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AccessGroup group = groupRepo.findById(id).orElseThrow();
        
        // Security check: ensure the logged-in master owns this group
        if (group.getMaster().getUsername().equals(auth.getName())) {
            groupRepo.delete(group);
        }
        
        return "redirect:/master/dashboard";
    }
}
