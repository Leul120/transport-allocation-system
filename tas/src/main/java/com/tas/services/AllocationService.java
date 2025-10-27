package com.tas.services;

import com.tas.entities.Allocation;
import com.tas.entities.Station;
import com.tas.entities.User;
import com.tas.responses.DashboardResponse;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.io.Serializable;
import java.util.List;

public interface AllocationService {
    void createAllocation();
    Allocation getAllocationStatus(Long id);
    Allocation getAllocationByUser(Long id);
    Allocation updateAllocationStatus(Long id);
    void deleteAllocation(Long id);
    DashboardResponse dashboard();
    void updateAllocation(Long id);
    List<Allocation> getAllAllocations();

}
