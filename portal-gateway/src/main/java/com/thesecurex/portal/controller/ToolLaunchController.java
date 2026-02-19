package com.thesecurex.portal.controller;

import com.thesecurex.portal.entity.LaunchToken;
import com.thesecurex.portal.model.Tool;
import com.thesecurex.portal.repository.ToolRepository;
import com.thesecurex.portal.service.ToolLaunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class ToolLaunchController {

    @Autowired
    private ToolLaunchService toolLaunchService;

    @Autowired
    private ToolRepository toolRepository;

    @Autowired
    private com.thesecurex.portal.service.UserService userService;

    @GetMapping("/tools/launch/{toolId}")
    public String launchTool(@PathVariable Long toolId, Authentication authentication) {
        String username = authentication.getName();
        
        // Security Check: Verify user has access to this tool
        boolean hasAccess = userService.getToolsForUser(username).stream()
                .anyMatch(t -> t.getId().equals(toolId));
        
        if (!hasAccess) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to access this tool.");
        }
        
        // verify tool exists
        Optional<Tool> toolOpt = toolRepository.findById(toolId);
        if (toolOpt.isEmpty()) {
            return "redirect:/dashboard?error=ToolNotFound";
        }
        
        Tool tool = toolOpt.get();
        LaunchToken token = toolLaunchService.generateToken(username, toolId);
        
        // Construct redirect URL
        // Assuming tool.endpoint is the full URL to the tool's entry point
        // e.g., http://localhost:8000/sso
        String redirectUrl = tool.getEndpoint();
        if (redirectUrl.contains("?")) {
            redirectUrl += "&token=" + token.getToken();
        } else {
            redirectUrl += "?token=" + token.getToken();
        }
        
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/api/tools/verify")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestParam String token) {
        Optional<LaunchToken> tokenOpt = toolLaunchService.verifyAndBurnToken(token);
        
        Map<String, Object> response = new HashMap<>();
        if (tokenOpt.isPresent()) {
            LaunchToken validToken = tokenOpt.get();
            response.put("valid", true);
            response.put("username", validToken.getUsername());
            response.put("toolId", validToken.getToolId());
            
            // Optionally include tool details if needed by the consumer
            toolRepository.findById(validToken.getToolId()).ifPresent(t -> {
                response.put("toolName", t.getName());
                response.put("toolEndpoint", t.getEndpoint());
            });
            
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
