package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.EventRegistrationDTO;
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

        String message = "Pending join request for " + event.getName();
        notificationService.createNotification(user, message, event);
        eventRegistrationRepository.save(registration);
        return new ResponseEntity<>("Registered Successfully. Please wait for approval.", HttpStatus.OK);
    }

    public ResponseEntity<EventRegistrationDTO> getEventRegistrationByEventAndUser(Long eventId, Long userId) {
    Optional<Event> optionalEvent = eventRepository.findById(eventId);
    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalEvent.isPresent() && optionalUser.isPresent()) {
        Event event = optionalEvent.get();
        User user = optionalUser.get();

        Optional<EventRegistration> optionalEventRegistration = eventRegistrationRepository.findByEventAndUser(event, user);
        if (optionalEventRegistration.isPresent()) {
            EventRegistration eventRegistration = optionalEventRegistration.get();
            EventRegistrationDTO dto = new EventRegistrationDTO();
            dto.setId(eventRegistration.getId());
            dto.setUserId(eventRegistration.getUser().getUserID());
            dto.setUsername(eventRegistration.getUser().getUsername());
            dto.setFullName(eventRegistration.getUser().getFirstName() + " " + eventRegistration.getUser().getLastName());
            dto.setEventId(eventRegistration.getEvent().getId());
            dto.setEventName(eventRegistration.getEvent().getName());
            dto.setStatus(eventRegistration.getStatus());
            dto.setRegisteredAt(eventRegistration.getRegisteredAt());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
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

            eventRegistrationRepository.delete(eventRegistration);

            String message = "Your join request for " + event.getName() + " has been canceled";
            notificationService.createNotification(user, message, event);
            return new ResponseEntity<>("Registration has been canceled", HttpStatus.OK);
        }
        return new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND);
    }

    //Displays all users that are registered to an event
    public ResponseEntity<List<EventRegistrationDTO>> getAllEventRegistrations(){
        List<EventRegistration> registrations = eventRegistrationRepository.findAll();
        if(registrations.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<EventRegistrationDTO> eventRegistrationDTOList = new ArrayList<>();
        for(EventRegistration eventRegistration : registrations){
            EventRegistrationDTO eventRegistrationDTO = new EventRegistrationDTO();
            eventRegistrationDTO.setEventName(eventRegistration.getEvent().getName());
            eventRegistrationDTO.setId(eventRegistration.getId());
            eventRegistrationDTO.setEventId(eventRegistration.getEvent().getId());
            eventRegistrationDTO.setUsername(eventRegistration.getUser().getUsername());
            String fullName = eventRegistration.getUser().getFirstName() + " " + eventRegistration.getUser().getLastName();
            eventRegistrationDTO.setFullName(fullName);
            eventRegistrationDTO.setUserId(eventRegistration.getUser().getUserID());
            eventRegistrationDTO.setStatus(eventRegistration.getStatus());
            eventRegistrationDTO.setRegisteredAt(eventRegistration.getRegisteredAt());
            eventRegistrationDTOList.add(eventRegistrationDTO);
        }
        return new ResponseEntity<>(eventRegistrationDTOList, HttpStatus.OK);
    }

    //This is accessed by organizers, so they can view the list of users who registered to the event
    public ResponseEntity<List<EventRegistrationDTO>> getAllRegisteredUsersToMyEvent(Long eventId, User user){
        Optional<User> userOptional = userRepository.findById(user.getUserID());
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);

        if(userOptional.isEmpty()){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
        if(eventOptional.isEmpty()){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}

        User user1 = userOptional.get();
        Event event = eventOptional.get();
        if(user1.getRole().equals("ORGANIZER") && event.getOrganizer().getUserID().equals(user1.getUserID())){
            List<EventRegistrationDTO> eventRegistrationDTOList = new ArrayList<>();
            for(EventRegistration registration : registrations){
                EventRegistrationDTO eventRegistrationDTO = new EventRegistrationDTO();
                eventRegistrationDTO.setId(registration.getId());
                eventRegistrationDTO.setUserId(registration.getUser().getUserID());
                eventRegistrationDTO.setUsername(registration.getUser().getUsername());
                eventRegistrationDTO.setFullName(registration.getUser().getFirstName() + " " + registration.getUser().getLastName());
                eventRegistrationDTO.setEventId(registration.getEvent().getId());
                eventRegistrationDTO.setEventName(registration.getEvent().getName());
                eventRegistrationDTO.setStatus(registration.getStatus());
                eventRegistrationDTO.setRegisteredAt(registration.getRegisteredAt());
                eventRegistrationDTOList.add(eventRegistrationDTO);
            }
            return new ResponseEntity<>(eventRegistrationDTOList, HttpStatus.OK);
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

    //Displays all the accepted join requests
    public ResponseEntity<List<EventRegistrationDTO>> getAllAcceptedRequest(){
        List<EventRegistration> registrations = eventRegistrationRepository.findAll();

        List<EventRegistrationDTO> acceptedRegistrations = new ArrayList<>();
        for(EventRegistration registration : registrations){
            EventRegistrationDTO eventRegistrationDTO = new EventRegistrationDTO();
            if(registration.getStatus().equals("Accepted")){
                eventRegistrationDTO.setId(registration.getId());
                eventRegistrationDTO.setUserId(registration.getUser().getUserID());
                eventRegistrationDTO.setUsername(registration.getUser().getUsername());
                eventRegistrationDTO.setFullName(registration.getUser().getFirstName() + " " + registration.getUser().getLastName());
                eventRegistrationDTO.setEventId(registration.getEvent().getId());
                eventRegistrationDTO.setEventName(registration.getEvent().getName());
                eventRegistrationDTO.setStatus(registration.getStatus());
                eventRegistrationDTO.setRegisteredAt(registration.getRegisteredAt());
                acceptedRegistrations.add(eventRegistrationDTO);
            }
        }
        return new ResponseEntity<>(acceptedRegistrations, HttpStatus.OK);
    }

}
