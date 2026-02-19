package com.thesecurex.portal.repository;

import com.thesecurex.portal.model.AccessGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessGroupRepository extends JpaRepository<AccessGroup, Long> {
    List<AccessGroup> findByMasterUsername(String username);
    
    // New method for OEM to see all groups or specific ones if needed
    // For now, OEM can see all, or we might want to filter by null master (system groups) if any
}
