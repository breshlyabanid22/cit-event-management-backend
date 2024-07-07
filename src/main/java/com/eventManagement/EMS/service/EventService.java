package com.eventManagement.EMS.service;

import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.EventRepository;
import com.eventManagement.EMS.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    VenueRepository venueRepository;

    public ResponseEntity<String> createEvent(Event event, User user){
        List<Event> conflictingEvents = eventRepository.findByVenueAndTimeRange(
                event.getVenue(),
                event.getStartTime(),
                event.getEndTime()
        );

        if(!conflictingEvents.isEmpty()){
            return new ResponseEntity<>("The venue is already reserved for the specified time range", HttpStatus.CONFLICT);
        }

        event.setOrganizer(user);
        eventRepository.save(event);
        return new ResponseEntity<>("Event created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Event>> getAllEventsByVenue(Long venueId, User user) {
        // Check if user is an organizer or admin
        if (user.getRole().equals("ORGANIZER") || user.getRole().equals("ADMIN")) {
            // Check if user is assigned to the venue or is an admin
            if (user.getRole().equals("ADMIN") || user.getManagedVenues().stream().anyMatch(v -> v.getId().equals(venueId))) {
                List<Event> events = eventRepository.findByVenueId(venueId);
                return new ResponseEntity<>(events, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<List<Event>> getAllEvents(){
        List<Event> events = eventRepository.findAll();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

}
