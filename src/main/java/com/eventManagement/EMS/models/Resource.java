package com.eventManagement.EMS.models;


import jakarta.persistence.*;

@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 500)
    private String description;

    private String type;

    private boolean availability = true;

    @ManyToOne
    @JoinColumn(name = "eventId")
    private Event eventResource;

    public Resource() {
    }

    public Resource(Long id, String name, String description, String type, boolean availability, Event eventResource) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.availability = availability;
        this.eventResource = eventResource;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }


    public Event getEventResource() {
        return eventResource;
    }

    public void setEventResource(Event eventResource) {
        this.eventResource = eventResource;
    }
}
