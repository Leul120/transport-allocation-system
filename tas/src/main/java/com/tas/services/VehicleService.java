package com.tas.services;

import com.tas.entities.Location;
import com.tas.entities.Vehicle;
import com.tas.entities.VehicleStatus;
import com.tas.requests.LocationRequest;

import java.util.List;

public interface VehicleService {
    Vehicle addVehicle(Vehicle vehicle);
    List<Vehicle> getAllVehicles();
    Vehicle getVehicleDetails(Long id);
    Vehicle updateVehicle(Long id, LocationRequest locationRequest);
    Vehicle updateVehicleStatus(Long id, VehicleStatus status);
}
