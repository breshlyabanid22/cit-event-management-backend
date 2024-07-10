package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Venue> addVenue(@RequestBody Venue venue) {
        try {
            Venue savedVenue = venueService.addVenue(venue);
            return new ResponseEntity<>(savedVenue, HttpStatus.CREATED);
            } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
}

    //Gets all venues
    @GetMapping("/all")//This can be used when displaying all available venues
    public ResponseEntity<List<Venue>> getAllVenues(){
        return venueService.getAll();
    }

    @DeleteMapping("/delete/{venueId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteVenue(@PathVariable Long venueId){
        return venueService.deleteVenue(venueId);
    }




}
