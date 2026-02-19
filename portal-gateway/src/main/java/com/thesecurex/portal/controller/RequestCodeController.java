package com.thesecurex.portal.controller;

import com.thesecurex.portal.model.TrialRequest;
import com.thesecurex.portal.repository.TrialRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RequestCodeController {

    @Autowired
    private TrialRequestRepository trialRequestRepository;

    @GetMapping("/request-code")
    public String requestCodePage() {
        return "request-code";
    }

    @PostMapping("/request-code")
    public String processRequest(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String countryCode,
            @RequestParam String phoneNumber,
            @RequestParam String address,
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam String pincode,
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "false") boolean termsAccepted,
            Model model) {

        if (!termsAccepted) {
            model.addAttribute("error", "You must accept the Terms and Conditions.");
            return "request-code";
        }

        TrialRequest request = new TrialRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setCountryCode(countryCode);
        request.setPhoneNumber(phoneNumber);
        request.setAddress(address);
        request.setCountry(country);
        request.setCity(city);
        request.setPincode(pincode);
        request.setReason(reason);

        trialRequestRepository.save(request);

        return "redirect:/request-code?success";
    }
}
