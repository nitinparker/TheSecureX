package com.thesecurex.portal.repository;

import com.thesecurex.portal.model.Evidence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByUploadedByUsername(String username);
}
