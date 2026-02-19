package com.thesecurex.portal.repository;

import com.thesecurex.portal.model.TrialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrialRequestRepository extends JpaRepository<TrialRequest, Long> {
    List<TrialRequest> findByProcessedFalse();
}
