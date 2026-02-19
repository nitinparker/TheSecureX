package com.thesecurex.portal.repository;

import com.thesecurex.portal.model.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.time.LocalDateTime;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    Optional<InviteCode> findByCode(String code);
    long countByUsedFalseAndExpiresAtAfter(LocalDateTime now);
}
