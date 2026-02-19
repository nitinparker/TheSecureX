package com.thesecurex.portal;

import com.thesecurex.portal.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ToolAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "restricted_user", roles = {"USER"})
    public void testUnauthorizedToolLaunch() throws Exception {
        // Mock the user service to return no tools for this user
        when(userService.getToolsForUser("restricted_user")).thenReturn(Collections.emptyList());

        // Assume Tool ID 99 is 'TheNetProtectX' and is NOT in this user's group
        mockMvc.perform(get("/tools/launch/99"))
               .andExpect(status().isForbidden());
               // Expect 403 because the User Access Enforcement logic should block this
    }

    @Test
    @WithMockUser(username = "oem_admin", roles = {"OEM"})
    public void testOEMDashboardAccess() throws Exception {
        mockMvc.perform(get("/admin/invites"))
               .andExpect(status().isOk());
    }
}
