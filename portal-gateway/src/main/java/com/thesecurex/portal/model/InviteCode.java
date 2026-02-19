package com.thesecurex.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "invite_codes")
@Data
@NoArgsConstructor
public class InviteCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private Role roleToAssign;

    @ManyToOne
    @JoinColumn(name = "target_group_id")
    private AccessGroup targetGroup;

    private boolean used = false;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
