package com.eventManagement.EMS.DTO;


public class ResourceDTO {

    //    {
//        "name" : "",
//        "description": "",
//        "type": "",  //equipment, personel, room
//        "availability": true,
//    }
    private Long id;

    private String name;

    private String description;

    private String type;

    private boolean availability;

    private String event;

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

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
