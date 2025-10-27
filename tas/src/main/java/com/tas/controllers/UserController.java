package com.tas.controllers;

import com.tas.entities.Location;
import com.tas.entities.VehicleStatus;
import com.tas.repositories.UserRepository;
import com.tas.requests.LocationRequest;
import com.tas.responses.ApiResponse;
import com.tas.services.AllocationService;
import com.tas.services.AuthenticationService;
import com.tas.services.StationService;
import com.tas.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private UserRepository userRepository;

    private final StationService stationService;
    private final VehicleService vehicleService;
    private final AllocationService allocationService;
    @Autowired
    public  UserController(AuthenticationService authenticationService, UserRepository userRepository, StationService stationService, VehicleService vehicleService,AllocationService allocationService){
        this.stationService=stationService;
        this.userRepository=userRepository;
        this.vehicleService=vehicleService;
        this.allocationService=allocationService;
    }
    @GetMapping("/")
    public ResponseEntity<ApiResponse> getSmt(){
        return ResponseEntity.ok(new ApiResponse("success","jhk"));
    }

    @GetMapping("/get-allocation")
    public ResponseEntity<ApiResponse> getAllocation(@RequestAttribute("vehicle") Long id){
        try {
            return ResponseEntity.ok(new ApiResponse("success",allocationService.getAllocationByUser(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/get-vehicle")
    public ResponseEntity<ApiResponse> getVehicle(@RequestAttribute("vehicle") Long id){
        try {
            return ResponseEntity.ok(new ApiResponse("success",vehicleService.getVehicleDetails(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/update-allocation-status/{id}")
    public ResponseEntity<ApiResponse> updateAllocationStatus(@PathVariable Long id){
        try {
            return ResponseEntity.ok(new ApiResponse("success",allocationService.updateAllocationStatus(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping(path = "/update-vehicle-status/{status}")
    public ResponseEntity<ApiResponse> updateAllocationStatus(@RequestAttribute("vehicle") Long id,@PathVariable VehicleStatus status){
        try {
            return ResponseEntity.ok(new ApiResponse("success",vehicleService.updateVehicleStatus(id,status)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PutMapping("/update-vehicle")
    public ResponseEntity<ApiResponse> updateVehicle(@RequestAttribute("vehicle") Long id,@RequestBody LocationRequest locationRequest){
        try {
            return ResponseEntity.ok(new ApiResponse("success",vehicleService.updateVehicle(id,locationRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
