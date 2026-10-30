package com.tn.smarthospital.repository;

import com.tn.smarthospital.model.Doctor;
import com.tn.smarthospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByActiveTrue();

    List<Doctor> findByHospitalIdAndActiveTrue(Long hospitalId);

    Optional<Doctor> findByUser(User user);

    @Query("SELECT DISTINCT d.specialty FROM Doctor d WHERE d.active = true ORDER BY d.specialty ASC")
    List<String> findDistinctSpecialties();

    @Query("SELECT d FROM Doctor d WHERE d.active = true " +
           "AND (:district IS NULL OR :district = '' OR LOWER(d.hospital.district) = LOWER(:district)) " +
           "AND (:specialty IS NULL OR :specialty = '' OR LOWER(d.specialty) = LOWER(:specialty)) " +
           "AND (:hospitalId IS NULL OR d.hospital.id = :hospitalId) " +
           "ORDER BY d.name ASC")
    List<Doctor> searchDoctors(@Param("district") String district,
                               @Param("specialty") String specialty,
                               @Param("hospitalId") Long hospitalId);
}

