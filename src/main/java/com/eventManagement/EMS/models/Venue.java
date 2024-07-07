package com.eventManagement.EMS.models;


import jakarta.persistence.*;

import java.util.List;

@Entity
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;

    @ManyToOne
    @JoinColumn(name = "venue_manager_id")
    private User venueManager;


    public Venue() {}

    public Venue(Long id, String name, String location, List<Event> events, User venueManager) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.events = events;
        this.venueManager = venueManager;
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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public User getVenueManager() {
        return venueManager;
    }

    public void setVenueManager(User venueManager) {
        this.venueManager = venueManager;
    }
}
