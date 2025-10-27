package com.tas.services;

import com.tas.entities.Station;
import com.tas.requests.PersonCountRequest;
import com.tas.requests.StationRequest;

import java.util.List;

public interface StationService {
    Station addStation(StationRequest stationRequest);
    List<Station> getAllStations();
    Station getStationDetails(Long id);
    void deleteStation(Long id);
    Station addPersonCount(Long id, PersonCountRequest personCountRequest);
    Station updateStation(Long id,StationRequest stationRequest);
}
