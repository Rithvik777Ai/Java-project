package com.tn.smarthospital.repository;

import com.tn.smarthospital.model.AvailabilitySlot;
import com.tn.smarthospital.model.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByDoctorIdAndDateAndStatusOrderByStartTimeAsc(Long doctorId, LocalDate date, SlotStatus status);

    List<AvailabilitySlot> findByDoctorIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(Long doctorId, LocalDate date);

    List<AvailabilitySlot> findByDoctorIdAndDateOrderByStartTimeAsc(Long doctorId, LocalDate date);

    Optional<AvailabilitySlot> findByDoctorIdAndDateAndStartTime(Long doctorId, LocalDate date, LocalTime startTime);

    @Query("SELECT DISTINCT s.date FROM AvailabilitySlot s WHERE s.doctor.id = :doctorId AND s.date >= :startDate AND s.status = 'AVAILABLE' ORDER BY s.date ASC")
    List<LocalDate> findAvailableDatesForDoctor(@Param("doctorId") Long doctorId, @Param("startDate") LocalDate startDate);
}

