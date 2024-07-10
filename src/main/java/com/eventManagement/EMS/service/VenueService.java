package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.VenueDTO;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    @Autowired
    VenueRepository venueRepository;
    @Autowired
    UserRepository userRepository;

    public ResponseEntity<String> addVenue(VenueDTO venueDTO){
        List<User> managers = userRepository.findAllById(venueDTO.getVenueManagerIds());
        if(managers.isEmpty() || managers.size() != venueDTO.getVenueManagerIds().size()){
            return new ResponseEntity<>("One or more users not found", HttpStatus.NOT_FOUND);
        }

        Venue venue = new Venue();
        venue.setVenueManager(managers);
        venue.setName(venueDTO.getName());
        venue.setLocation(venueDTO.getLocation());
        venue.setMaxCapacity(venueDTO.getMaxCapacity());

        venueRepository.save(venue);

        return new ResponseEntity<>("Venue added successfully", HttpStatus.CREATED);
    }
    public ResponseEntity<List<Venue>> getAll(){
        List<Venue> venues = venueRepository.findAll();
        return new ResponseEntity<>(venues, HttpStatus.OK);
    }
}
