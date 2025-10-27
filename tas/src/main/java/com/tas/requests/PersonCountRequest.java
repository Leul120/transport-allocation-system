package com.tas.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonCountRequest {
    private Long stationId;
    private LocalDateTime time;
    private Integer count;
}
