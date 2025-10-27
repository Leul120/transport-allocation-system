package com.tas.responses;

import com.tas.entities.Allocation;
import com.tas.entities.Station;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private Integer totalStations;
    private Integer totalVehicles;
    private Integer busesAllocated;
    private Integer taxisAllocated;
    private Integer activeStations;
    private Integer availableVehicles;
    private Long busAllocationRate;
    private Long taxiAllocationRate;
    private List<Allocation> allocations;
    private List<StationWithCount> stations;
}
