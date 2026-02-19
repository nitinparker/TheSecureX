package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tools")
@Data
@NoArgsConstructor
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "TheLogX"

    private String description;
    
    private String icon; // e.g., "fa-bug" for fontawesome

    private String endpoint; // URL to launch the tool
}
