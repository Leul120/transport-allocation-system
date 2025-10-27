package com.tas.repositories;

import com.tas.entities.Station;
import com.tas.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station,Long> {
    List<Station> findByStatus(Status status);
}
