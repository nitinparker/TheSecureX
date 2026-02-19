package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_requests")
@Data
@NoArgsConstructor
public class TrialRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String address;
    private String country;
    private String city;
    private String pincode;
    
    @Column(columnDefinition = "TEXT")
    private String reason;

    private boolean processed = false;
    private LocalDateTime requestDate = LocalDateTime.now();
}
