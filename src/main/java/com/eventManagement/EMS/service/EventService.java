package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.EventDTO;
import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.EventRepository;
import com.eventManagement.EMS.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    VenueRepository venueRepository;
    public ResponseEntity<String> createEvent(EventDTO eventDTO, User user){
        Optional<Venue> eventVenueOpt = venueRepository.findById(eventDTO.getVenueId());
        if(eventDTO.getName() == null || eventDTO.getStartTime() == null || eventDTO.getEndTime() == null || eventDTO.getCapacity() <= 0){
            return new ResponseEntity<>("Invalid event data", HttpStatus.BAD_REQUEST);
        }
        if(!eventVenueOpt.isPresent()){
            return new ResponseEntity<>("Venue not found", HttpStatus.NOT_FOUND);
        }

        List<Event> conflictingEvents = eventRepository.findByVenueAndTimeRange(
                eventDTO.getVenueId(),
                eventDTO.getStartTime(),
                eventDTO.getEndTime()
        );
        if(!conflictingEvents.isEmpty()){
            return new ResponseEntity<>("The venue is already reserved for the specified date", HttpStatus.CONFLICT);
        }
        Venue eventVenue = eventVenueOpt.get();
        Event event = new Event();
        event.setVenue(eventVenue);
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setCapacity(eventDTO.getCapacity());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setStatus("PENDING");
        event.setOrganizer(user);
        event.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, MMM dd yyyy")));
        eventRepository.save(event);
        return new ResponseEntity<>("Event created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Event>> getAllEventsByVenue(Long venueId, User user) {
        // Check if user is an organizer or admin
        if (user.getRole().equals("VENUE_MANAGER") || user.getRole().equals("ADMIN")) {
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
    public ResponseEntity<Event> getEventById(Long eventId){
        Optional <Event> eventOpt = eventRepository.findById(eventId);

        if(eventOpt.isPresent()){
            return new ResponseEntity<>(eventOpt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> updateEvent(Long eventId, EventDTO updatedEventDTO, User user){
        Optional<Event> existingEventOpt = eventRepository.findById(eventId);
        Optional<Venue> venueEventOpt = venueRepository.findById(updatedEventDTO.getVenueId());

        if(existingEventOpt.isPresent()){
            Event existingEvent = existingEventOpt.get();

            //Check if user is admin or organizer
            if(user.getRole().equals("ADMIN") || existingEvent.getOrganizer().getUserID().equals(user.getUserID())){

                List<Event> conflictingEvents =eventRepository.findByVenueAndTimeRange(
                        updatedEventDTO.getVenueId(),
                        updatedEventDTO.getStartTime(),
                        updatedEventDTO.getEndTime()
                );

                // Exclude the event being updated from conflict checks
                conflictingEvents.removeIf(event -> event.getId().equals(eventId));

                if(!conflictingEvents.isEmpty()){
                    return new ResponseEntity<>("The venue is already reserved for the specified date", HttpStatus.CONFLICT);
                }
                Venue eventVenue = venueEventOpt.get();
                //update the event details
                existingEvent.setName(updatedEventDTO.getName() != null ? updatedEventDTO.getName() : existingEvent.getName());
                existingEvent.setDescription(updatedEventDTO.getDescription() != null ? updatedEventDTO.getDescription() : existingEvent.getDescription());
                existingEvent.setCapacity(updatedEventDTO.getCapacity() != existingEvent.getCapacity() ? updatedEventDTO.getCapacity() : existingEvent.getCapacity());
                existingEvent.setVenue(eventVenue != null ? eventVenue : existingEvent.getVenue());
                existingEvent.setStartTime(updatedEventDTO.getStartTime() != null ? updatedEventDTO.getStartTime() : existingEvent.getStartTime());
                existingEvent.setEndTime(updatedEventDTO.getEndTime() != null ? updatedEventDTO.getEndTime() : existingEvent.getEndTime());
                existingEvent.setStatus(updatedEventDTO.getStatus() != null ? updatedEventDTO.getStatus() : existingEvent.getStatus());

                eventRepository.save(existingEvent);
                return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("You are not authorized to update this event", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<String> cancelEvent(Long eventId, User user){
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if(eventOpt.isPresent()){
            Event event = eventOpt.get();

            //if user is the organizer or admin, allow to delete.
            if(user.getRole().equals("ADMIN") || event.getOrganizer().getUserID().equals(user.getUserID())){
                eventRepository.delete(event);
                return new ResponseEntity<>("Event has been cancelled", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("You are not authorized to cancel this event", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
    }


}
