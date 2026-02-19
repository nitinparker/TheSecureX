package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.AccessGroup;
import com.thesecurex.portal.model.MasterGeoData;
import com.thesecurex.portal.model.Tool;
import com.thesecurex.portal.model.TrialRequest;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.AccessGroupRepository;
import com.thesecurex.portal.repository.MasterGeoDataRepository;
import com.thesecurex.portal.repository.ToolRepository;
import com.thesecurex.portal.repository.TrialRequestRepository;
import com.thesecurex.portal.repository.UserRepository;
import com.thesecurex.portal.service.EmailService;
import com.thesecurex.portal.service.InviteCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/oem")
@PreAuthorize("hasRole('OEM')")
public class OEMController {

    @Autowired
    private MasterGeoDataRepository masterGeoDataRepository;

    @Autowired
    private TrialRequestRepository trialRequestRepository;

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AccessGroupRepository accessGroupRepository;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long pendingRequests = trialRequestRepository.findByProcessedFalse().size();
        model.addAttribute("pendingRequests", pendingRequests);
        return "oem/dashboard";
    }

    @GetMapping("/master-geo")
    public String listMasterGeoData(Model model) {
        List<MasterGeoData> data = masterGeoDataRepository.findAll();
        model.addAttribute("geoDataList", data);
        return "oem/master-geo";
    }

    @PostMapping("/master-geo/add")
    public String addMasterGeoData(@ModelAttribute MasterGeoData data) {
        masterGeoDataRepository.save(data);
        return "redirect:/oem/master-geo";
    }

    @PostMapping("/master-geo/delete/{id}")
    public String deleteMasterGeoData(@PathVariable Long id) {
        masterGeoDataRepository.deleteById(id);
        return "redirect:/oem/master-geo";
    }

    @GetMapping("/trial-requests")
    public String listTrialRequests(Model model) {
        List<TrialRequest> requests = trialRequestRepository.findByProcessedFalse();
        List<AccessGroup> accessGroups = accessGroupRepository.findAll(); // Get all access groups
        model.addAttribute("requests", requests);
        model.addAttribute("accessGroups", accessGroups);
        return "oem/trial-requests";
    }

    @PostMapping("/trial-requests/approve/{id}")
    public String approveTrialRequest(
            @PathVariable Long id, 
            @RequestParam int validDays,
            @RequestParam Long accessGroupId,
            RedirectAttributes redirectAttributes) {
        
        TrialRequest request = trialRequestRepository.findById(id).orElseThrow();
        AccessGroup group = accessGroupRepository.findById(accessGroupId).orElseThrow();
        
        // Generate a USER invite code valid for selected days with specific access group
        String code = inviteCodeService.createInviteCodeWithGroup("USER", validDays, group);
        
        // Send Email
        try {
            emailService.sendInviteEmail(request.getEmail(), code, request.getFirstName());
            redirectAttributes.addFlashAttribute("successMessage", "Request approved. Code sent to " + request.getEmail());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successMessage", "Request approved but email failed. Code: " + code);
        }
        
        request.setProcessed(true);
        trialRequestRepository.save(request);
        
        return "redirect:/oem/trial-requests";
    }

    @PostMapping("/trial-requests/reject/{id}")
    public String rejectTrialRequest(@PathVariable Long id) {
        TrialRequest request = trialRequestRepository.findById(id).orElseThrow();
        request.setProcessed(true); // Just mark as processed without generating code
        trialRequestRepository.save(request);
        return "redirect:/oem/trial-requests";
    }

    // Access Group Management
    @GetMapping("/access-groups")
    public String listAccessGroups(Model model) {
        List<AccessGroup> groups = accessGroupRepository.findAll();
        List<Tool> allTools = toolRepository.findAll();
        model.addAttribute("groups", groups);
        model.addAttribute("allTools", allTools);
        return "oem/access-groups";
    }

    @PostMapping("/access-groups/create")
    public String createAccessGroup(
            @RequestParam String name, 
            @RequestParam(required = false) List<Long> toolIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User oemUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        
        AccessGroup group = new AccessGroup();
        group.setName(name);
        group.setMaster(oemUser); // OEM is the master of this group
        
        if (toolIds != null) {
            List<Tool> tools = toolRepository.findAllById(toolIds);
            group.setAllowedTools(tools);
        }
        
        accessGroupRepository.save(group);
        return "redirect:/oem/access-groups";
    }
}
