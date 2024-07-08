package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventService eventService;


    @PostMapping("/create") //Create an event
    public ResponseEntity<String> createEvent(@RequestBody Event event, @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.createEvent(event, user);
    }

    @GetMapping("/venue/{venueId}") //Find an event by Venue
    public ResponseEntity<List<Event>> getAllEventsByVenue(
            @PathVariable Long venueId,
            @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.getAllEventsByVenue(venueId, user);
    }

    @GetMapping("/all") // Get all events
    public ResponseEntity<List<Event>> getAllEvents(){
        return eventService.getAllEvents();
    }


    @PutMapping("/update/{eventId}") //Update an event
    public ResponseEntity<String> updateEvent(@PathVariable Long eventId, @RequestBody Event updatedEvent, @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.updateEvent(eventId, updatedEvent, user);
    }

    @DeleteMapping("/cancel/{eventId}") //Cancel or delete an event
    public ResponseEntity<String> cancelEvent(@PathVariable Long eventId, @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.cancelEvent(eventId, user);
    }

    @GetMapping("/{eventId}") //Endpoint to display the details of a single event
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId){
        return eventService.getEventById(eventId);
    }
}
