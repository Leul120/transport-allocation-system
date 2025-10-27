package com.tas.controllers;


import com.tas.requests.StationRequest;
import com.tas.responses.ApiResponse;
import com.tas.services.AllocationService;
import com.tas.services.StationService;
import com.tas.services.UserService;
import com.tas.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AllocationService allocationService;
    private final StationService stationService;
    private final VehicleService vehicleService;
    private final UserService userService;

    @Autowired
    public AdminController(AllocationService allocationService, StationService stationService, VehicleService vehicleService, UserService userService) {
        this.stationService = stationService;
        this.allocationService = allocationService;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getSmt() {
        return ResponseEntity.ok(new ApiResponse("success", "jhk"));
    }

    @GetMapping("/get-dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        try {
            return ResponseEntity.ok(new ApiResponse("success", allocationService.dashboard()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/get-all-stations")
    public ResponseEntity<ApiResponse> getAllStations() {
        try {
            return ResponseEntity.ok(new ApiResponse("success", stationService.getAllStations()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/get-all-vehicles")
    public ResponseEntity<ApiResponse> getAllVehicles() {
        try {
            return ResponseEntity.ok(new ApiResponse("success", vehicleService.getAllVehicles()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/get-all-allocations")
    public ResponseEntity<ApiResponse> getAllAllocations() {
        try {
            return ResponseEntity.ok(new ApiResponse("success", allocationService.getAllAllocations()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            return ResponseEntity.ok(new ApiResponse("success", userService.getAllUser()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/update-station/{id}")
    public ResponseEntity<ApiResponse> updateStation(@PathVariable Long id, @RequestBody StationRequest stationRequest) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", stationService.updateStation(id, stationRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/get-station/{id}")
    public ResponseEntity<ApiResponse> getStation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", stationService.getStationDetails(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @DeleteMapping("/delete-station/{id}")
    public ResponseEntity<ApiResponse> deleteStation(@PathVariable Long id) {
        try {
            stationService.deleteStation(id);
            return ResponseEntity.ok(new ApiResponse("success", "deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }



    @DeleteMapping("/delete-allocation/{id}")
    public ResponseEntity<ApiResponse> deleteAllocation(@PathVariable Long id) {
        try {
            allocationService.deleteAllocation(id);
            return ResponseEntity.ok(new ApiResponse("success", "deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @PostMapping("/add-station")
    public ResponseEntity<ApiResponse> addStation(@RequestBody StationRequest stationRequest) {
        try {
            return ResponseEntity.ok(new ApiResponse("success", stationService.addStation(stationRequest)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}

