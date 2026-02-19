package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ManyToOne
    @JoinColumn(name = "master_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User master; // The Master who created this group

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "group_tool_mapping",
        joinColumns = @JoinColumn(name = "group_id"),
        inverseJoinColumns = @JoinColumn(name = "tool_id")
    )
    private List<Tool> allowedTools;

    @OneToMany(mappedBy = "accessGroup")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> members;
}
