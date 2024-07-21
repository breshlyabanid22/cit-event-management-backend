package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.DTO.VenueDTO;
import com.eventManagement.EMS.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/venues")
@RestController
public class VenueController {

    @Autowired
    VenueService venueService;


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") //Only admin users can add a venue
    public ResponseEntity<String> addVenue(
            @RequestPart("venueDTO") VenueDTO venueDTO,
            @RequestPart("imageFiles") List<MultipartFile> imageFiles) {
            return venueService.addVenue(venueDTO, imageFiles);
    }

    //Gets all venues
    @GetMapping//This can be used when displaying all available venues
    public ResponseEntity<List<VenueDTO>> getAllVenues(){
        return venueService.getAll();
    }

    @PatchMapping("/{venueId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateVenue(
            @PathVariable Long venueId,
            @RequestPart("venueDTO") VenueDTO updatedVenue,
            @RequestPart("imageFiles") List<MultipartFile> imageFiles){
        return venueService.updateVenue(venueId, updatedVenue, imageFiles);
    }


    @DeleteMapping("/{venueId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteVenue(@PathVariable Long venueId){
        return venueService.deleteVenue(venueId);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteAllVenues(){
        return venueService.deleteAll();
    }



}
