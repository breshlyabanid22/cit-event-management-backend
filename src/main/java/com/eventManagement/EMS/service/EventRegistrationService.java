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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class EventRegistrationService {

    @Autowired
    EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    EventRepository eventRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    UserRepository userRepository;
    public ResponseEntity<String> registerToEvent(Long eventId, Long userId){
        Optional<Event> eventOpt = eventRepository.findById(eventId);

        if(eventOpt.isEmpty()){
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
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

        if(eventOpt.isEmpty()){ return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);}
        if(userOpt.isEmpty()){return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);}

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
    public ResponseEntity<List<EventRegistration>> getAllEventRegistrations(){
        List<EventRegistration> registrations = eventRegistrationRepository.findAll();
        if(registrations.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    //This is accessed by organizers, so they can view the list of users who registered to the event
    public ResponseEntity<List<EventRegistration>> getAllRegisteredUsersToMyEvent(Long eventId, User user){
        Optional<User> userOptional = userRepository.findById(user.getUserID());
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);

        if(userOptional.isEmpty()){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        if(eventOptional.isEmpty()){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}

        User user1 = userOptional.get();
        Event event = eventOptional.get();
        if(user1.getRole().equals("ORGANIZER") && event.getOrganizer().getUserID().equals(user1.getUserID())){
            return new ResponseEntity<>(registrations, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
    //This is accessible by organizers of a specific events
    public ResponseEntity<String> acceptRegistrationRequest(Long registrationId, User user){
        Optional<EventRegistration> registrationOptional = eventRegistrationRepository.findById(registrationId);

        if(registrationOptional.isEmpty()){ return new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND);}
        EventRegistration registration = registrationOptional.get();
        Long eventId = registration.getEvent().getId();
        //Checks if the user is the organizer of the event
        if(user.getOrganizedEvents().stream().anyMatch(e -> e.getId().equals(eventId))){
            registration.setStatus("Accepted");

            String message = "Hi " + registration.getUser().getFirstName() + ", your join request to " + registration.getEvent().getName() + " has been approved.";
            notificationService.createNotification(registration.getUser(), message, registration.getEvent());
            return new ResponseEntity<>("Your join request has been accepted", HttpStatus.ACCEPTED);
        }

        return new ResponseEntity<>("User is not an organizer to this event", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> declineRegistrationRequest(Long registrationId, User user){
        Optional<EventRegistration> registrationOptional = eventRegistrationRepository.findById(registrationId);

        if(registrationOptional.isEmpty()){ return new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND);}

        EventRegistration registration = registrationOptional.get();
        Long eventId = registration.getEvent().getId();
        if(user.getOrganizedEvents().stream().anyMatch(e -> e.getId().equals(eventId))){
            registration.setStatus("Declined");
            String message = "Sorry " + registration.getUser().getFirstName() + ", but your join request to " + registration.getEvent().getName() + " has been declined.";
            notificationService.createNotification(registration.getUser(), message, registration.getEvent());
            return new ResponseEntity<>("Your join request has been declined", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("User is not an organizer to this event", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<EventRegistration>> getAllAcceptedRequest(){
        List<EventRegistration> registrations = eventRegistrationRepository.findAll();

        List<EventRegistration> acceptedRegistrations = new ArrayList<>();
        for(EventRegistration registration : registrations){
            if(registration.getStatus().equals("Accepted")){
                acceptedRegistrations.add(registration);
            }
        }
        return new ResponseEntity<>(acceptedRegistrations, HttpStatus.OK);
    }

}
