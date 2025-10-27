package com.stationCamera.config;

import com.stationCamera.services.ImageProcessingService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Configuration
public class TaskSchedulerConfig {
    RestClient restClient=RestClient.builder().build();

    private final TaskScheduler taskScheduler;
    private final ImageProcessingService imageProcessingService;
//    private final AllocationService allocationService;
    @Autowired
//    public TaskSchedulerConfig(AllocationService allocationService,TaskScheduler taskScheduler){
//        this.allocationService=allocationService;
//        this.taskScheduler=taskScheduler;
//    }

    public TaskSchedulerConfig(ImageProcessingService imageProcessingService) {
        this.imageProcessingService=imageProcessingService;
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


//        Integer result=imageProcessingService.processLatestImage();
//        System.out.println(result);
//        PersonCountRequest count=new PersonCountRequest();
////        count.setStationId(3L);
//        count.setCount(result);
//        count.setTime(new Date().toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime());
//        String response=restClient.post()
//                .uri("http://localhost/8081/api/v1/auth/add-person-count/3")
////                .header("Authorization","Bearer ")
//                .body(count)
//                .retrieve()
//                .body(String.class);
//        System.out.println(response);
//        allocationService.createAllocation(station);

    }
}

