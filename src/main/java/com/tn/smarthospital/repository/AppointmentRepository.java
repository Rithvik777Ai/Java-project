package com.tn.smarthospital.repository;

import com.tn.smarthospital.model.Appointment;
import com.tn.smarthospital.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByBookingReference(String bookingReference);

    List<Appointment> findByPatientIdOrderByDateDescTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByDateDescTimeDesc(Long doctorId);

    List<Appointment> findByHospitalIdOrderByDateDescTimeDesc(Long hospitalId);

    List<Appointment> findAllByOrderByDateDescTimeDesc();

    boolean existsByPatientIdAndSlotIdAndStatusNot(Long patientId, Long slotId, AppointmentStatus status);

    boolean existsBySlotIdAndStatusNot(Long slotId, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:date IS NULL OR a.date = :date) " +
           "AND (:hospitalId IS NULL OR a.hospital.id = :hospitalId) " +
           "AND (:doctorId IS NULL OR a.doctor.id = :doctorId) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "ORDER BY a.date DESC, a.time DESC")
    List<Appointment> searchAppointmentsAdmin(@Param("date") LocalDate date,
                                             @Param("hospitalId") Long hospitalId,
                                             @Param("doctorId") Long doctorId,
                                             @Param("status") AppointmentStatus status);

    long countByStatus(AppointmentStatus status);
}

