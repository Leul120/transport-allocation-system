package com.tas.config;

import com.tas.entities.Station;
import com.tas.services.impl.AllocationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


import java.time.Instant;

@Configuration
public class TaskSchedulerConfig {

    private final TaskScheduler taskScheduler;
    private final AllocationService allocationService;
    @Autowired
    public TaskSchedulerConfig(AllocationService allocationService) {
        this.allocationService = allocationService;
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.initialize();
        this.taskScheduler = threadPoolTaskScheduler;
    }

    @PostConstruct
    public void scheduleTask() {
        taskScheduler.scheduleAtFixedRate(this::runTask, 10000); // Run every 10 seconds
    }

    private void runTask() {
        System.out.println("Task executed at: " + Instant.now());
        // Add your business logic here
        Station station=new Station();
        allocationService.createAllocation();
    }
}

