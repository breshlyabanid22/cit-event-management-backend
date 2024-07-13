package com.eventManagement.EMS.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    private int maxCapacity;

    @OneToMany(mappedBy = "venue")
    private List<Event> events;


    @ManyToMany
    @JoinTable(
            name = "venue_managers",
            joinColumns = @JoinColumn(name = "venue_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> venueManagers = new ArrayList<>();

    public Venue() {}

    public Venue(Long id, String name, String location, int maxCapacity, List<Event> events, List<User> venueManagers) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.events = events;
        this.venueManagers = venueManagers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<User> getVenueManagers() {
        return venueManagers;
    }

    public void setVenueManagers(List<User> venueManagers) {
        this.venueManagers = venueManagers;
    }
}
