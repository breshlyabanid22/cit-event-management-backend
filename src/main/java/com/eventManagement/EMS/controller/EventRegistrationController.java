package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.DTO.EventRegistrationDTO;
import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/registrations")
@RestController
public class EventRegistrationController {

    @Autowired
    EventRegistrationService eventRegistrationService;

    // localhost:8080/users?eventId=&userId=3   (sample usage)
    @PostMapping("/{eventId}/{userId}")//Registers a user to an event
    public ResponseEntity<String> registerToEvent(@PathVariable Long eventId,
                                                  @PathVariable Long userId){
        return eventRegistrationService.registerToEvent(eventId, userId);
    }

    // localhost:8080/users?eventId=1&userId=3   (sample usage)
    @DeleteMapping("/{eventId}/{userId}")// When participants want to cancel the registration
    public ResponseEntity<String> cancelRegistration(@PathVariable Long eventId,
                                                     @PathVariable Long userId){
        return eventRegistrationService.cancelRegistration(eventId, userId);
    }

    @GetMapping//Retrieves all
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<EventRegistrationDTO>> getAllEventRegistrations(){
        return eventRegistrationService.getAllEventRegistrations();
    }

    @GetMapping("/registered/{eventId}")
    @PreAuthorize("hasAuthority('ORGANIZER')") //Display this in an organizers dashboard
    public ResponseEntity<List<EventRegistrationDTO>> getAllRegisteredUsersToMyEvent(@PathVariable Long eventId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        User user = userInfoDetails.getUser();
        return eventRegistrationService.getAllRegisteredUsersToMyEvent(eventId, user);
    }

    @PatchMapping("/{registrationId}/accept") // This accepts the user's event registration
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<String> acceptRegistrationRequest(@PathVariable Long registrationId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return eventRegistrationService.acceptRegistrationRequest(registrationId, user);
    }

    @PatchMapping("/{registrationId}/reject")// This rejects the user's event registration
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public ResponseEntity<String> declineRegistrationRequest(@PathVariable Long registrationId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return eventRegistrationService.declineRegistrationRequest(registrationId, user);
    }

    @GetMapping("/accepted")//Displays all accepted users' event registrations
    @PreAuthorize("hasAuthority('ORGANIZER') || hasAuthority('ADMIN')")
    public ResponseEntity<List<EventRegistrationDTO>> getAllAcceptedRequest(){
        return eventRegistrationService.getAllAcceptedRequest();
    }
}
