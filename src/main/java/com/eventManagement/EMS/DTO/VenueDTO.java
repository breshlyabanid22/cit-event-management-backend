package com.eventManagement.EMS.DTO;

import java.util.ArrayList;
import java.util.List;

public class VenueDTO {


    //    {
//        "name": "",
//        "location": "",
//        "maxCapacity": "",
//        "venueManagersID": [1 , 2 ,3] //An array of userIds
//    }

    private Long id;

    private String name;
    private String location;

    private int maxCapacity;

    private List<String> events;
    private List<Long> venueManagersID;
    private List<String> venueManagers;

    private String imagePath;


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

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getVenueManagers() {
        return venueManagers;
    }

    public void setVenueManagers(List<String> venueManagers) {
        this.venueManagers = venueManagers;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Long> getVenueManagersID() {
        return venueManagersID;
    }

    public void setVenueManagersID(List<Long> venueManagersID) {
        this.venueManagersID = venueManagersID;
    }
}
