package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "access_groups")
@Data
@NoArgsConstructor
public class AccessGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "master_id")
    private Long masterId; // The Master who created this group

    // In a real app, you'd have a ManyToMany relationship with Tools here
}
