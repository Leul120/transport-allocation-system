package com.tas.repositories;

import com.tas.entities.Allocation;
import com.tas.entities.AllocationStatus;
import com.tas.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AllocationRepository extends JpaRepository<Allocation,Long> {
    List<Allocation> findByStatusAndVehicle_id(AllocationStatus status, Long id);
    List<Allocation> findByVehicle_id( Long id);
//    List<Allocation> findByTimestampAndVehicle_Type(LocalDateTime time, Type type);
    @Query("SELECT a FROM Allocation a " +
            "WHERE a.timeStamp >= :startTime " +
            "AND a.timeStamp <= :endTime")
    List<Allocation> findTodayAllocations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    @Query("SELECT a FROM Allocation a " +
            "WHERE a.timeStamp >= :startTime " +
            "AND a.timeStamp <= :endTime " +
            "AND a.station.id = :stationId"
    )
    List<Allocation> findTodayAllocationsWithStation(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("stationId") Long stationId);
}
