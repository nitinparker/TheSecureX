package com.thesecurex.portal.repository;

import com.thesecurex.portal.entity.LaunchToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaunchTokenRepository extends JpaRepository<LaunchToken, Long> {
    Optional<LaunchToken> findByToken(String token);
}
