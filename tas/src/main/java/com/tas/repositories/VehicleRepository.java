package com.tas.repositories;

import com.tas.entities.User;
import com.tas.entities.Vehicle;
import com.tas.entities.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {
    List<Vehicle> findByStatus(VehicleStatus status);
    Vehicle findByUser(User user);
}
