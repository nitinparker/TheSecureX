package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;
    
    // Profile Fields
    private String firstName;
    private String middleName;
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String countryCode;
    private String phoneNumber;
    private String address;
    private String city;
    private String pincode;
    private String country;

    @Enumerated(EnumType.STRING)
    private Role role; // OEM, MASTER, USER

    @ManyToOne
    @JoinColumn(name = "access_group_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AccessGroup accessGroup;
}
