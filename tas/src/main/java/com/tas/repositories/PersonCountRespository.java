package com.tas.repositories;

import com.tas.entities.PersonCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonCountRespository extends JpaRepository<PersonCount,Long> {
    @Query("SELECT p FROM PersonCount p " +
            "WHERE p.time >= :startTime " +
            "AND p.time <= :endTime")
    List<PersonCount> findTodayPersonCounts(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
