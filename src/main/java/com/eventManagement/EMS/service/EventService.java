package com.eventManagement.EMS.service;

import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public ResponseEntity<String> createEvent(Event event, User user){

        if(event.getName() == null || event.getStartTime() == null || event.getEndTime() == null || event.getCapacity() <= 0){
            return new ResponseEntity<>("Invalid event data", HttpStatus.BAD_REQUEST);
        }

        List<Event> conflictingEvents = eventRepository.findByVenueAndTimeRange(
                event.getVenue(),
                event.getStartTime(),
                event.getEndTime()
        );
        if(!conflictingEvents.isEmpty()){
            return new ResponseEntity<>("The venue is already reserved for the specified date", HttpStatus.CONFLICT);
        }

        event.setOrganizer(user);
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

    public ResponseEntity<String> updateEvent(Long eventId, Event updatedEvent, User user){
        Optional<Event> existingEventOpt = eventRepository.findById(eventId);

        if(existingEventOpt.isPresent()){
            Event existingEvent = existingEventOpt.get();

            //Check if user is admin or organizer
            if(user.getRole().equals("ADMIN") || existingEvent.getOrganizer().getUserID().equals(user.getUserID())){

                List<Event> conflictingEvents =eventRepository.findByVenueAndTimeRange(
                        updatedEvent.getVenue(),
                        updatedEvent.getStartTime(),
                        updatedEvent.getEndTime()
                );

                // Exclude the event being updated from conflict checks
                conflictingEvents.removeIf(event -> event.getId().equals(eventId));

                if(!conflictingEvents.isEmpty()){
                    return new ResponseEntity<>("The venue is already reserved for the specified date", HttpStatus.CONFLICT);
                }
                //update the event details
                existingEvent.setName(updatedEvent.getName());
                existingEvent.setDescription(updatedEvent.getDescription());
                existingEvent.setCapacity(updatedEvent.getCapacity());
                existingEvent.setVenue(updatedEvent.getVenue());
                existingEvent.setStartTime(updatedEvent.getStartTime());
                existingEvent.setEndTime(updatedEvent.getEndTime());

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
