package com.tas.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StationWithCount {
    private Long stationId;
    private String name;
    private double averagePersonCount;
    private LocalDateTime peakHours;
    private Integer allocations;
}
