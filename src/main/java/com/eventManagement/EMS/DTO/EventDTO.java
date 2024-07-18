package com.eventManagement.EMS.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class EventDTO {
    //    {
//        "name": "",
//        "description": "",
//        "startTime": "",
//        "endTime": "",
//        "venueId": 1,  //venueId
//        "organizer": 2, //userId
//        "resourceId": [ 1, 2, 3] //An array of resourceId
//    }
    private Long id;

    private String name;

    private String description;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    private String organizer;
    private List<Long> resourceId;
    private List<String> resourceName;

    private String venueName;
    private Long venueId;

    private String status;

    private String imagePath;


    public Long getId() {
        return id;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
    public List<Long> getResourceIDs() {
        return resourceId;
    }

    public void setResourceIDs(List<Long> resourceId) {
        this.resourceId = resourceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getResourceName() {
        return resourceName;
    }

    public void setResourceName(List<String> resourceName) {
        this.resourceName = resourceName;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public String toString() {
        return "EventDTO{" +
                "resourceId=" + resourceId +
                " name=" + name +
                " venueId=" + venueId +
                " startTime=" + startTime +
                " endTime=" + endTime +
                '}';
    }
}
