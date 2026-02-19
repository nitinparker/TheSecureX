package com.thesecurex.portal.repository;

import com.thesecurex.portal.model.MasterGeoData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MasterGeoDataRepository extends JpaRepository<MasterGeoData, Long> {
    List<MasterGeoData> findByCountry(String country);
    List<MasterGeoData> findByPincode(String pincode);
}
