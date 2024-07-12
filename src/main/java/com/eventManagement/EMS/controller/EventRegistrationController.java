package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.EventRegistration;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<EventRegistration>> getAllEventRegistrations(){
        return eventRegistrationService.getAllEventRegistrations();
    }

    @GetMapping("/registered/{eventId}")
    @PreAuthorize("hasAuthority('ORGANIZER')") //Display this in an organizers dashboard
    public ResponseEntity<List<EventRegistration>> getAllRegisteredUsersToMyEvent(@PathVariable Long eventId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        User user = userInfoDetails.getUser();
        return eventRegistrationService.getAllRegisteredUsersToMyEvent(eventId, user);
    }

    @PostMapping("/registered/accept/{registrationId}") // This accepts the user's event registration
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<String> acceptRegistrationRequest(@PathVariable Long registrationId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return eventRegistrationService.acceptRegistrationRequest(registrationId, user);
    }

    @PostMapping("/registered/reject/{registrationId}")// This rejects the user's event registration
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<String> declineRegistrationRequest(@PathVariable Long registrationId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return eventRegistrationService.declineRegistrationRequest(registrationId, user);
    }

    @GetMapping("/registrations/accepted/all")//Displays all accepted users' event registrations
    @PreAuthorize("hasAuthority('ORGANIZER') || hasAuthority('ADMIN')")
    public ResponseEntity<List<EventRegistration>> getAllAcceptedRequest(){
        return eventRegistrationService.getAllAcceptedRequest();
    }
}
