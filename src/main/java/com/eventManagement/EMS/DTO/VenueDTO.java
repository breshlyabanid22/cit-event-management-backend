package com.eventManagement.EMS.DTO;

import java.util.List;

public class VenueDTO {
    private String name;
    private String location;

    private List<Long> venueManagerIds;
    private int maxCapacity;

    // Getters and setters
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

    public List<Long> getVenueManagerIds() {
        return venueManagerIds;
    }

    public void setVenueManagerId(List<Long> venueManagerIds) {
        this.venueManagerIds = venueManagerIds;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
