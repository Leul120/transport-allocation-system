package com.tas.requests;

import com.tas.entities.Location;
import com.tas.entities.Status;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class StationRequest {
    private String name;
    private Location location;
    private Status status;
}
