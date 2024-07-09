package com.eventManagement.EMS.service;

import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VenueService {

    @Autowired
    VenueRepository venueRepository;


    public ResponseEntity<String> addVenue(Venue venue, User user){
        Optional<Venue> venueOpt = venueRepository.findByName(venue.getName());

        //If venue does not exist yet
        if(venueOpt.isEmpty()){
            //save the venue
            venueRepository.save(venue);
            return new ResponseEntity<>("Venue successfully added", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("Venue already exist. Please try again.", HttpStatus.CONFLICT);
        }
    }
    public ResponseEntity<List<Venue>> getAll(){
        List<Venue> venues = venueRepository.findAll();
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }
}
