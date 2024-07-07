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
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    EventService eventService;


    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody Event event, @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.createEvent(event, user);
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<Event>> getAllEventsByVenue(
            @PathVariable Long venueId,
            @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return eventService.getAllEventsByVenue(venueId, user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents(){
        return eventService.getAllEvents();
    }


}
