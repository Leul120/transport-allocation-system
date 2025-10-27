package com.tas.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Allocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Station station;
    @ManyToOne
    private Vehicle vehicle;
    @CreationTimestamp
    private LocalDateTime timeStamp;
    private AllocationStatus status;

}
