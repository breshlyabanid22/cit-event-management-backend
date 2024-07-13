package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.VenueDTO;
import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.repository.VenueRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VenueService {

    @Autowired
    VenueRepository venueRepository;
    @Autowired
    UserRepository userRepository;


    public Venue addVenue(Venue venue) {
        if (venue.getVenueManagers() == null) {
            venue.setVenueManagers(new ArrayList<>());
        }
        List<User> managers = new ArrayList<>();
        for (User manager : venue.getVenueManagers()) {
            if (manager.getUserID() == null) {
                throw new IllegalArgumentException("Venue manager ID must not be null");
            }
            User foundManager = userRepository.findById(manager.getUserID())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid venue manager ID: " + manager.getUserID()));
            managers.add(foundManager);
            foundManager.setRole("VENUE_MANAGER");
        }
        venue.setVenueManagers(managers);
        return venueRepository.save(venue);


    }
    public ResponseEntity<List<VenueDTO>> getAll(){
        List<Venue> venues = venueRepository.findAll();

        List<VenueDTO> venueDTOList = new ArrayList<>();
        for(Venue venue : venues){
            VenueDTO venueDTO = new VenueDTO();
            venueDTO.setId(venue.getId());
            venueDTO.setLocation(venue.getLocation());
            venueDTO.setMaxCapacity(venue.getMaxCapacity());
            venueDTO.setVenueManagers(venue.getVenueManagers().stream().map(User::getFirstName).toList());
            venueDTO.setEvents(venue.getEvents().stream().map(Event::getName).toList());
            venueDTO.setName(venue.getName());
            venueDTOList.add(venueDTO);
        }
        return new ResponseEntity<>(venueDTOList, HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<String> deleteVenue(Long venueId){
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new EntityNotFoundException("Venue not found with id " + venueId));

        if(venue.getVenueManagers() != null){
            List<User> managers = new ArrayList<>();
            for(User manager : venue.getVenueManagers()){
                User foundManager = userRepository.findById(manager.getUserID())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid venue manager ID: " + manager.getUserID()));
                foundManager.setRole("USER");
            }
            venue.getVenueManagers().clear();
        }

        venueRepository.delete(venue);
        return new ResponseEntity<>("Venue successfully deleted", HttpStatus.NO_CONTENT);
    }
}
