package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.models.EventRegistration;
import com.eventManagement.EMS.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/events/")
@RestController
public class EventRegistrationController {

    @Autowired
    EventRegistrationService eventRegistrationService;


    @PostMapping("{eventId}/register") //Registers a user to an event
    public ResponseEntity<String> registerToEvent(@PathVariable Long eventId, @RequestParam Long userId){
        return eventRegistrationService.registerToEvent(eventId, userId);
    }

    @PostMapping("{eventId}/cancel")// When participants want to cancel the registration
    public ResponseEntity<String> cancelRegistration(@PathVariable Long eventId, @RequestParam Long userId){
        return eventRegistrationService.cancelRegistration(eventId, userId);
    }

    @GetMapping("/{eventId}/registrations") //Retrieves all
    public ResponseEntity<List<EventRegistration>> getAllRegisteredUsersByEvent(@PathVariable Long eventId){
        return eventRegistrationService.getAllEventRegistrationsByEvent(eventId);
    }



}
