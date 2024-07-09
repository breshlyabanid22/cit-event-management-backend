package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/events/")
@RestController
public class EventRegistrationController {

    @Autowired
    EventRegistrationService eventRegistrationService;


    @PostMapping("{evenId}/register")
    public ResponseEntity<String> registerToEvent(@PathVariable Long eventId, @RequestParam Long userId){
        return eventRegistrationService.registerToEvent(eventId, userId);
    }


}
