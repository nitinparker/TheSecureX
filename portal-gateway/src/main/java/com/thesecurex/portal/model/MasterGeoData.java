package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "master_geo_data")
@Data
@NoArgsConstructor
public class MasterGeoData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String countryCode; // e.g., +1, +91

    private String state;
    private String city;
    private String pincode;
}
