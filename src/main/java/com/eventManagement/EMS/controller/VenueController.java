package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.DTO.VenueDTO;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> addVenue(@RequestBody VenueDTO venueDTO){
        return venueService.addVenue(venueDTO);
    }

    //Gets all venues
    @GetMapping("/all")//This can be used when displaying all available venues
    public ResponseEntity<List<Venue>> getAllVenues(){
        return venueService.getAll();
    }



}
