package com.eventManagement.EMS.service;

import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.EventRegistration;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.EventRegistrationRepository;
import com.eventManagement.EMS.repository.EventRepository;
import com.eventManagement.EMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Service
public class EventRegistrationService {

    @Autowired
    EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;
    public ResponseEntity<String> registerToEvent(Long eventId, Long userId){
        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if(!eventOpt.isPresent()){
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if(!userOpt.isPresent()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        Event event = eventOpt.get();
        User user = userOpt.get();

        //Check if user is already registered
        Optional<EventRegistration> existingRegistration = eventRegistrationRepository.findByEventAndUser(event, user);
        if(existingRegistration.isPresent()){
            return new ResponseEntity<>("You are already registered for this event", HttpStatus.CONFLICT);

        }
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setStatus("Pending");
        registration.setRegisteredAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, MMM dd yyyy")));

        eventRegistrationRepository.save(registration);
        return new ResponseEntity<>("Registered Successfully. Please wait for approval.", HttpStatus.OK);
    }

    public ResponseEntity<String> cancelRegistration(Long eventId, Long userId){
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<User> userOpt = userRepository.findById(userId);

        Event event = eventOpt.get();
        User user = userOpt.get();

        //Check if the registration exist
        Optional<EventRegistration> existingRegistration = eventRegistrationRepository.findByEventAndUser(event, user);
        if(existingRegistration.isPresent()){
            EventRegistration eventRegistration = existingRegistration.get();
            eventRegistration.setStatus("Canceled");
            return new ResponseEntity<>("Registration has been canceled", HttpStatus.OK);
        }
        return new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND);
    }

    //Displays all users that are registered to a specific event
    public ResponseEntity<List<EventRegistration>> getAllEventRegistrationsByEvent(Long eventId){
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);

        if(registrations.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }


}
