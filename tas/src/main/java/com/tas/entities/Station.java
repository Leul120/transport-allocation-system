package com.tas.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private Location location;
    private Status status;
    @OneToMany(mappedBy = "station",cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PersonCount> personCount = new ArrayList<>();;
    public void addPersonCount(PersonCount personCount) {
        this.personCount.add(personCount);
        personCount.setStation(this);
    }

}
