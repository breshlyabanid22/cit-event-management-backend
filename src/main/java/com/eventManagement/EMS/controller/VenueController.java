package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/venue")
@RestController
public class VenueController {

    @Autowired
    VenueService venueService;


    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')") //Only admin users can add a venue
    public ResponseEntity<String> addVenue(@RequestBody Venue venue,
                                           @AuthenticationPrincipal UserInfoDetails userDetails){
        User user = userDetails.getUser();
        return venueService.addVenue(venue, user);
    }

    //Gets all venues
    @GetMapping("/all")//This can be used when displaying all available venues
    public ResponseEntity<List<Venue>> getAllVenues(){
        return venueService.getAll();
    }



}
