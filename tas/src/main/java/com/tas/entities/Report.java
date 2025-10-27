package com.tas.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Station station;
    @CreationTimestamp
    private LocalDateTime timeStamp;
    private Integer total_allocations;
    private Long average_wait_time;
    private Integer max_density;
}
