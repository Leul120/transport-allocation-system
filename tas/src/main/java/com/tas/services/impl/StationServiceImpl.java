package com.tas.services.impl;

import com.tas.entities.PersonCount;
import com.tas.entities.Station;
import com.tas.repositories.StationRepository;
import com.tas.requests.PersonCountRequest;
import com.tas.requests.StationRequest;
import com.tas.services.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public StationServiceImpl(StationRepository stationRepository){
        this.stationRepository=stationRepository;
    }
    @Override
    public Station addStation(StationRequest stationRequest) {
        try {
            Station station=new Station();
            station.setName(stationRequest.getName());
            station.setLocation(stationRequest.getLocation());
            station.setStatus(stationRequest.getStatus());
            station=stationRepository.save(station);
            messagingTemplate.convertAndSend("/update-station","update");
            return station;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Station> getAllStations() {
        try {
            return stationRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Station getStationDetails(Long id) {
        try {
            return stationRepository.findById(id).orElseThrow(()->new RuntimeException("Station not found!"));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteStation(Long id) {
        try {
             stationRepository.deleteById(id);
            messagingTemplate.convertAndSend("/topic/update-station","update");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Station updateStation(Long id,StationRequest stationRequest){
        Station station=stationRepository.findById(id).orElseThrow(()->new RuntimeException("error"));
        station.setName(stationRequest.getName());
        station.setLocation(stationRequest.getLocation());
        station.setStatus(stationRequest.getStatus());
        station=stationRepository.save(station);
        messagingTemplate.convertAndSend("/topic/update-station","update");
        return station;
    }
    @Override
    public Station addPersonCount(Long id, PersonCountRequest personCountRequest){
        try {
            Station station=stationRepository.findById(id).orElseThrow(()->new RuntimeException("Station not found!"));
            PersonCount personCount=new PersonCount();
            personCount.setCount(personCountRequest.getCount());
            personCount.setStation(station);
            personCount.setTime(LocalDateTime.now());
            station.addPersonCount(personCount);
            station= stationRepository.save(station);
            messagingTemplate.convertAndSend("/topic/update-station","update");
            return station;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
