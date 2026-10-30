package com.tn.smarthospital.repository;

import com.tn.smarthospital.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByActiveTrue();

    List<Hospital> findByIdInAndActiveTrue(List<Long> ids);

    @Query("SELECT DISTINCT h.district FROM Hospital h WHERE h.active = true ORDER BY h.district ASC")
    List<String> findDistinctDistricts();

    @Query("SELECT DISTINCT h.hospitalType FROM Hospital h WHERE h.active = true ORDER BY h.hospitalType ASC")
    List<String> findDistinctHospitalTypes();

    @Query("SELECT h FROM Hospital h WHERE h.active = true " +
           "AND (:district IS NULL OR :district = '' OR LOWER(h.district) = LOWER(:district)) " +
           "AND (:city IS NULL OR :city = '' OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
           "AND (:hospitalType IS NULL OR :hospitalType = '' OR LOWER(h.hospitalType) = LOWER(:hospitalType)) " +
           "AND (:specialty IS NULL OR :specialty = '' OR LOWER(h.departments) LIKE LOWER(CONCAT('%', :specialty, '%'))) " +
           "AND (:hasEmergency IS NULL OR h.hasEmergencyServices = :hasEmergency) " +
           "AND (:hasIcu IS NULL OR h.hasIcu = :hasIcu) " +
           "AND (:query IS NULL OR :query = '' OR " +
                "LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(h.address) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(h.city) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(h.departments) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Hospital> searchHospitals(@Param("district") String district,
                                  @Param("city") String city,
                                  @Param("hospitalType") String hospitalType,
                                  @Param("specialty") String specialty,
                                  @Param("hasEmergency") Boolean hasEmergency,
                                  @Param("hasIcu") Boolean hasIcu,
                                  @Param("query") String query);
}

