package com.tas.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.tas.entities.*;
import com.tas.repositories.*;
import com.tas.responses.DashboardResponse;
import com.tas.responses.StationWithCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllocationService implements com.tas.services.AllocationService {
    private final AllocationRepository allocationRepository;
    private final VehicleRepository vehicleRepository;
    private final LocationRepository locationRepository;
    private final StationRepository stationRepository;
    private final PersonCountRespository personCountRespository;
    @Value("${mapbox_key}")
    private String mapboxToken;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public AllocationService(AllocationRepository allocationRepository,PersonCountRespository personCountRespository,StationRepository stationRepository,VehicleRepository vehicleRepository,LocationRepository locationRepository){
        this.allocationRepository=allocationRepository;
        this.vehicleRepository=vehicleRepository;
        this.locationRepository=locationRepository;
        this.stationRepository=stationRepository;
        this.personCountRespository=personCountRespository;
    }

    public void updateLocation(User user,Location location){
       Vehicle vehicle=vehicleRepository.findByUser(user);
       vehicle.setLocation(location);
       vehicleRepository.save(vehicle);
    }


    public Vehicle findClosestVehicle(Station station) {
        List<Vehicle> vehicles = vehicleRepository.findByStatus(VehicleStatus.Available);
        System.out.println("vehicle"+vehicles);
        RestTemplate restTemplate = new RestTemplate();

        return vehicles.stream()
                .min((v1, v2) -> {
                    double distance1 = getDistance(v1.getLocation(), station.getLocation(), restTemplate);
                    System.out.println("distance :"+distance1);
                    double distance2 = getDistance(v2.getLocation(), station.getLocation(), restTemplate);
                    System.out.println("distance2 :"+distance2);
                    return Double.compare(distance1, distance2);
                })
                .orElse(null);
    }

    private double getDistance(Location vehicleLocation, Location stationLocation, RestTemplate restTemplate) {
        String url = String.format(
                "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f?geometries=geojson&access_token=%s",
                vehicleLocation.getLatitude(), vehicleLocation.getLongitude(),
                stationLocation.getLatitude(), stationLocation.getLongitude(), mapboxToken
        );

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response != null && response.has("routes") && response.get("routes").isArray()) {
            JsonNode routes = response.get("routes");
            if (routes.size() > 0 && routes.get(0).has("distance")) {
                return routes.get(0).get("distance").asDouble();
            }
        }
        throw new RuntimeException("Unable to fetch distance from Mapbox API");
    }

    @Override
    @Transactional
    public void createAllocation() {
        try {
            List<Station> filteredStations = stationRepository.findByStatus(Status.ACTIVE)
                    .stream()
                    .filter(station -> {
                        List<PersonCount> personCounts = station.getPersonCount();
                        int size = personCounts.size();
                        if(size>=5) {
                            List<PersonCount> lastFive = personCounts.subList(Math.max(0, size - 5), size);
                            return lastFive.stream().allMatch(count -> count.getCount() > 3);
                        }else{
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
            if(filteredStations==null){
                System.out.println("stationbgg ");
            }

            for(Station station:filteredStations){

                Allocation allocation=new Allocation();
                Vehicle vehicle=findClosestVehicle(station);
                System.out.println(station.getLocation());
                if (vehicle == null) {
                    throw new RuntimeException("No available vehicles for station: " + station.getId());
                }
                vehicle.setStatus(VehicleStatus.pending);
                station.setStatus(Status.PENDING);
                stationRepository.save(station);
                allocation.setVehicle(vehicle);
                allocation.setStation(station);
                allocation.setStatus(AllocationStatus.pending);
                allocationRepository.save(allocation);
                messagingTemplate.convertAndSend("/topic/update-allocation","update");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public List<Allocation> getAllAllocations(){
        return allocationRepository.findAll();
    }
    @Override
    public Allocation updateAllocationStatus(Long id){
        Allocation allocation= allocationRepository.findById(id).orElseThrow(()->new RuntimeException("Allocation not found!"));
        allocation.setStatus(AllocationStatus.taken);
        allocation= allocationRepository.save(allocation);
        messagingTemplate.convertAndSend("/topic/update-allocation","update");
        return allocation;

    }

    @Override
    public Allocation getAllocationStatus(Long id) {
        return allocationRepository.findById(id).orElseThrow(()->new RuntimeException("Allocation not found!"));
    }
    @Override
    public void deleteAllocation(Long id){
        try {
            allocationRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void updateAllocation( Long id){
        try {
            List<Allocation> allocations=allocationRepository.findByStatusAndVehicle_id(AllocationStatus.taken,id);
            Allocation allocation=allocations.get(allocations.size() - 1);
            Vehicle vehicle=vehicleRepository.findById(id).orElseThrow(()->new RuntimeException("error"));

            if(vehicle.getLocation().equals(allocation.getStation().getLocation())) {
                allocation.setStatus(AllocationStatus.completed);
                vehicle.setStatus(null);
                Station station = stationRepository.findById(allocation.getStation().getId()).orElseThrow(() -> new RuntimeException("error"));
                station.setStatus(Status.ACTIVE);
                stationRepository.save(station);
                vehicleRepository.save(vehicle);
                allocationRepository.save(allocation);
                messagingTemplate.convertAndSend("/topic/update-allocation","update");
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Allocation getAllocationByUser(Long id){
        Optional<Allocation> latestAllocation = allocationRepository
                .findByVehicle_id(id).stream().filter(a->!a.getStatus().equals(AllocationStatus.completed))
                .max(Comparator.comparing(Allocation::getTimeStamp));

        if (latestAllocation.isPresent()) {
            return latestAllocation.get();
            // Use the 'allocation' object here
        } else {
            // Handle the case where no allocation was found
            System.out.println("No allocation found for vehicle ID: " + id);
            throw new RuntimeException("No allocation found for vehicle ID: " + id);
        }

    }
    @Override
    public DashboardResponse dashboard(){
        DashboardResponse dashboardResponse=new DashboardResponse();
        LocalDate today = LocalDate.now();
        LocalDateTime startTime = today.atStartOfDay();
        LocalDateTime yesterdayStartTime = today.minusDays(1).atStartOfDay();
        LocalDateTime endTime = today.plusDays(1).atStartOfDay();
           dashboardResponse.setTotalStations(stationRepository.findAll().size());
           dashboardResponse.setTotalVehicles(vehicleRepository.findAll().size());
            List<Allocation> yesterdayAllocations=allocationRepository.findTodayAllocations(yesterdayStartTime,startTime);
            List<Allocation> allocations=allocationRepository.findTodayAllocations(startTime,endTime);
        dashboardResponse.setBusesAllocated(
                (int) allocations.stream()
                        .filter(a -> a.getVehicle().getType().equals(Type.Bus))
                        .count()
        );
        dashboardResponse.setTaxisAllocated(
                (int) allocations.stream()
                        .filter(a -> a.getVehicle().getType().equals(Type.Taxi))
                        .count()
        );
        dashboardResponse.setBusAllocationRate((long) ((allocations.stream().filter(a->a.getVehicle().getType().equals(Type.Bus)).toList().size()-yesterdayAllocations.stream().filter(a->a.getVehicle().getType().equals(Type.Bus)).toList().size())/100));
           dashboardResponse.setBusAllocationRate((long) ((allocations.stream().filter(a->a.getVehicle().getType().equals(Type.Taxi)).toList().size()-yesterdayAllocations.stream().filter(a->a.getVehicle().getType().equals(Type.Taxi)).toList().size())/100));
            dashboardResponse.setActiveStations((int) stationRepository.findByStatus(Status.ACTIVE).stream().count());
            dashboardResponse.setAvailableVehicles((int) vehicleRepository.findByStatus(VehicleStatus.Available).stream().count());
            dashboardResponse.setAllocations(allocationRepository.findTodayAllocations(startTime,endTime));
            personCountRespository.findTodayPersonCounts(startTime,endTime).stream().map(p->p.getStation());
        List<StationWithCount> stationsWithAverages = stationRepository.findAll()
                .stream()
                .map(s -> {
                    double averagePersonCount = s.getPersonCount()
                            .stream().filter(p->p.getTime().isAfter(startTime)&&p.getTime().isBefore(endTime))
                            .mapToInt(PersonCount::getCount)
                            .average()
                            .orElse(0.0);

                    // Handle the peak person count safely
                    PersonCount peak = s.getPersonCount()
                            .stream().filter(p->p.getTime().isAfter(startTime)&&p.getTime().isBefore(endTime))
                            .max(Comparator.comparing(PersonCount::getCount))
                            .orElse(null);


                    LocalDateTime peakTime = (peak != null) ? peak.getTime() : null;

                    int allocations1 = (int) allocationRepository.findTodayAllocationsWithStation(startTime, endTime, s.getId()).stream().count();


                    return new StationWithCount(s.getId(), s.getName(), averagePersonCount, peakTime, allocations1);
                })
                .toList();

        dashboardResponse.setStations(stationsWithAverages);

        return dashboardResponse;


    }
}
