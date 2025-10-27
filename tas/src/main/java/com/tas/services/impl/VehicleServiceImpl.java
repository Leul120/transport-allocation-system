package com.tas.services.impl;

import com.tas.entities.AllocationStatus;
import com.tas.entities.Location;
import com.tas.entities.Vehicle;
import com.tas.entities.VehicleStatus;
import com.tas.repositories.VehicleRepository;
import com.tas.requests.LocationRequest;
import com.tas.services.AllocationService;
import com.tas.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final AllocationService allocationService;
    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,AllocationService allocationService){
        this.vehicleRepository=vehicleRepository;
        this.allocationService=allocationService;
    }
    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return null;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        try {
            return vehicleRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehicle getVehicleDetails(Long id) {
        try {
            return vehicleRepository.findById(id).orElseThrow(()->new RuntimeException("Vehicle not found!"));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehicle updateVehicle(Long id, LocationRequest locationRequest){
        Vehicle vehicle=vehicleRepository.findById(id).orElseThrow(()->new RuntimeException("Vehicle not found!"));
        vehicle.getLocation().setLatitude(locationRequest.getLatitude());
        vehicle.getLocation().setLongitude(locationRequest.getLongitude());
        vehicle=vehicleRepository.save(vehicle);
        allocationService.updateAllocation(vehicle.getId());
        return vehicle;
    }
    @Override
    public Vehicle updateVehicleStatus(Long id, VehicleStatus status){
        Vehicle vehicle=vehicleRepository.findById(id).orElseThrow(()->new RuntimeException("Vehicle not found!"));
        if(!allocationService.getAllocationByUser(vehicle.getId()).getStatus().equals(AllocationStatus.completed) && status.equals(VehicleStatus.Available)) {
            vehicle.setStatus(VehicleStatus.pending);
        }else{
            vehicle.setStatus(status);
        }
        return vehicleRepository.save(vehicle);
    }
}
