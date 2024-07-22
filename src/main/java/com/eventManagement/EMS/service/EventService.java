package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.EventDTO;
import com.eventManagement.EMS.models.*;
import com.eventManagement.EMS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    VenueRepository venueRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    NotificationService notificationService;
    @Autowired
    EventRegistrationRepository eventRegistrationRepository;

    @Value("${upload.event.dir}")
    private String uploadDir;

    public ResponseEntity<String> createEvent(EventDTO eventDTO, MultipartFile imageFile, User user){
        Optional<Venue> eventVenueOpt = venueRepository.findById(eventDTO.getVenueId());
        if(eventDTO.getName() == null || eventDTO.getStartTime() == null || eventDTO.getEndTime() == null){
            return new ResponseEntity<>("Invalid event data", HttpStatus.BAD_REQUEST);
        }
        if(eventVenueOpt.isEmpty()){
            return new ResponseEntity<>("Venue not found", HttpStatus.NOT_FOUND);
        }

        List<Event> conflictingEvents = eventRepository.findByVenueAndTimeRange(
                eventDTO.getVenueId(),
                eventDTO.getStartTime(),
                eventDTO.getEndTime()
        );
        List<Event> activeConflictingEvents = conflictingEvents.stream()
                .filter(event -> !"Canceled".equalsIgnoreCase(event.getStatus()))
                .toList();
        if(!activeConflictingEvents.isEmpty()){
            return new ResponseEntity<>("The venue is already reserved for the specified date. Please try again.", HttpStatus.CONFLICT);
        }
        if(!imageFile.isEmpty()){
            try {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                if(imageFile.getOriginalFilename() != null){
                    Path filePath = uploadPath.resolve(imageFile.getOriginalFilename());
                    imageFile.transferTo(filePath.toFile());

                    eventDTO.setImagePath("/event-images/" + imageFile.getOriginalFilename());
                }
            }catch (IOException e){
                return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Venue eventVenue = eventVenueOpt.get();
        Event event = new Event();
        event.setVenue(eventVenue);
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setStatus("Pending");
        event.setOrganizer(user);
        event.setImagePath(eventDTO.getImagePath());
        event.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));

        List<Resource> resourceList = new ArrayList<>();
        List<Long> resourceIDs = eventDTO.getResourceIDs();

        if(resourceIDs == null || resourceIDs.isEmpty()){
            throw new IllegalArgumentException("Resource id must not be null");
        }
        for(Long resourceId : resourceIDs){
            if (resourceId == null) {
                throw new IllegalArgumentException("Venue manager ID must not be null");
            }
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid resource Id" + resourceId));
            resourceList.add(resource);
            event.setResources(resourceList);
        }
        eventRepository.save(event);
        //Send notifications to venue_managers
        List<User> venueManagers = eventVenue.getVenueManagers().stream().toList();
        String message = "New event request by " + user.getUsername();
        for(User manager: venueManagers){
            notificationService.createNotification(manager, message, event);
        }
        return new ResponseEntity<>("Event created successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<List<EventDTO>> getAllEventsByVenue(Long venueId, User user) {
        // Check if user is an organizer or admin
        if (user.getRole().equals("ORGANIZER") || user.getRole().equals("ADMIN")) {
            // Check if user is assigned to the venue or is an admin
            if (user.getRole().equals("ADMIN") || user.getManagedVenues().stream().anyMatch(v -> v.getId().equals(venueId))) {
                List<Event> events = eventRepository.findByVenueId(venueId);
                List<EventDTO> eventDTOList = new ArrayList<>();
                for(Event event : events){
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setId(event.getId());
                    eventDTO.setName(event.getName());
                    eventDTO.setDescription(event.getDescription());
                    eventDTO.setStartTime(event.getStartTime());
                    eventDTO.setEndTime(event.getEndTime());
                    eventDTO.setVenueName(event.getVenue().getName());
                    eventDTO.setOrganizer(event.getOrganizer().getUsername());
                    eventDTO.setImagePath(event.getImagePath());
                    eventDTO.setStatus(event.getStatus());
                    eventDTO.setResourceName(event.getResources().stream().map(Resource::getName).toList());
                    eventDTO.setVenueId(event.getVenue().getId());
                    eventDTOList.add(eventDTO);
                }
                return new ResponseEntity<>(eventDTOList, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<List<EventDTO>> getAllEvents(){
        List<Event> events = eventRepository.findAll();
        List<EventDTO> eventDTOList = new ArrayList<>();
        for(Event event : events){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            eventDTO.setName(event.getName());
            eventDTO.setDescription(event.getDescription());
            eventDTO.setStartTime(event.getStartTime());
            eventDTO.setEndTime(event.getEndTime());
            eventDTO.setVenueName(event.getVenue().getName());
            eventDTO.setOrganizer(event.getOrganizer().getUsername());
            eventDTO.setImagePath(event.getImagePath());
            eventDTO.setStatus(event.getStatus());
            eventDTO.setResourceName(event.getResources().stream().map(Resource::getName).toList());
            eventDTO.setVenueId(event.getVenue().getId());
            eventDTOList.add(eventDTO);
        }
        return new ResponseEntity<>(eventDTOList, HttpStatus.OK);
    }
    public ResponseEntity<List<EventDTO>> getEventsByOrganizer(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User organizer = userOptional.get();
        List<Event> events = eventRepository.findByOrganizer(organizer);
        List<EventDTO> eventDTOList = new ArrayList<>();
        for(Event event : events){
            EventDTO eventDTO = new EventDTO();
            eventDTO.setId(event.getId());
            eventDTO.setName(event.getName());
            eventDTO.setDescription(event.getDescription());
            eventDTO.setStartTime(event.getStartTime());
            eventDTO.setEndTime(event.getEndTime());
            eventDTO.setVenueName(event.getVenue().getName());
            eventDTO.setOrganizer(event.getOrganizer().getUsername());
            eventDTO.setImagePath(event.getImagePath());
            eventDTO.setStatus(event.getStatus());
            eventDTO.setResourceName(event.getResources().stream().map(Resource::getName).toList());
            eventDTO.setVenueId(event.getVenue().getId());
            eventDTOList.add(eventDTO);
        }
        return new ResponseEntity<>(eventDTOList, HttpStatus.OK);
    }

    public ResponseEntity<List<EventDTO>> getAllApprovedEvents() {
        List<Event> events = eventRepository.findAll();

        if (!events.isEmpty()) {
            List<EventDTO> approvedEvents = new ArrayList<>();
            for (Event event : events) {
                EventDTO eventDTO = new EventDTO();
                if (event.getStatus().equals("Approved")) {
                    eventDTO.setId(event.getId());
                    eventDTO.setName(event.getName());
                    eventDTO.setDescription(event.getDescription());
                    eventDTO.setStartTime(event.getStartTime());
                    eventDTO.setEndTime(event.getEndTime());
                    eventDTO.setVenueName(event.getVenue().getName());
                    eventDTO.setOrganizer(event.getOrganizer().getFirstName() + event.getOrganizer().getLastName());
                    eventDTO.setImagePath(event.getImagePath());
                    eventDTO.setStatus(event.getStatus());
                    eventDTO.setResourceName(event.getResources().stream().map(Resource::getName).toList());
                    eventDTO.setVenueId(event.getVenue().getId());
                    approvedEvents.add(eventDTO);
                }
            }
            return new ResponseEntity<>(approvedEvents, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<EventDTO> getEventById(Long eventId){
        Optional <Event> eventOpt = eventRepository.findById(eventId);
        if(eventOpt.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Event event = eventOpt.get();
        EventDTO eventDTO = new EventDTO();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getName());
        eventDTO.setDescription(event.getDescription());
        eventDTO.setStartTime(event.getStartTime());
        eventDTO.setEndTime(event.getEndTime());
        eventDTO.setVenueName(event.getVenue().getName());
        eventDTO.setOrganizer(event.getOrganizer().getUsername());
        eventDTO.setImagePath(event.getImagePath());
        eventDTO.setStatus(event.getStatus());
        eventDTO.setResourceName(event.getResources().stream().map(Resource::getName).toList());
        eventDTO.setVenueId(event.getVenue().getId());
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    public ResponseEntity<String> updateEvent(Long eventId, MultipartFile imageFile, EventDTO updatedEventDTO, User user){
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
                conflictingEvents.removeIf(event -> event.getId().equals(eventId));
                List<Event> activeConflictingEvents = conflictingEvents.stream()
                                .filter(event -> !"Canceled".equalsIgnoreCase(event.getStatus()))
                                        .toList();

                if(!activeConflictingEvents.isEmpty()){
                    return new ResponseEntity<>("The venue is already reserved for the specified date", HttpStatus.CONFLICT);
                }
                if(!imageFile.isEmpty()){

                    try {
                        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                        Files.createDirectories(uploadPath);
                        if(imageFile.getOriginalFilename() != null){
                            Path filePath = uploadPath.resolve(imageFile.getOriginalFilename());
                            imageFile.transferTo(filePath.toFile());

                            updatedEventDTO.setImagePath("/event-images/" + imageFile.getOriginalFilename());
                        }
                    }catch (IOException e){
                        return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                // Exclude the event being updated from conflict checks

                if(venueEventOpt.isPresent()){
                    Venue eventVenue = venueEventOpt.get();
                    existingEvent.setVenue(eventVenue);
                }else{
                    existingEvent.setVenue(existingEvent.getVenue());
                }
                //update the event details
                existingEvent.setName(updatedEventDTO.getName() != null ? updatedEventDTO.getName() : existingEvent.getName());
                existingEvent.setDescription(updatedEventDTO.getDescription() != null ? updatedEventDTO.getDescription() : existingEvent.getDescription());
                existingEvent.setStartTime(updatedEventDTO.getStartTime() != null ? updatedEventDTO.getStartTime() : existingEvent.getStartTime());
                existingEvent.setEndTime(updatedEventDTO.getEndTime() != null ? updatedEventDTO.getEndTime() : existingEvent.getEndTime());
                existingEvent.setStatus(updatedEventDTO.getStatus() != null ? updatedEventDTO.getStatus() : existingEvent.getStatus());
                existingEvent.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));

                eventRepository.save(existingEvent);
                return new ResponseEntity<>("Event updated successfully", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("You are not authorized to update this event", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
    }

    //This is accessed by admin or venue_manager when approving a proposed event
    public ResponseEntity<String> approveEvent(Long eventId, User user){
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            return new ResponseEntity<>("Event does not exist", HttpStatus.NOT_FOUND);
        }
        Event event = eventOptional.get();

        if((user.getRole().equals("ORGANIZER") && user.getUserType().equals("VENUE_MANAGER")) || user.getRole().equals("ADMIN")){
            event.setStatus("Approved");
            if(user.getRole().equals("PARTICIPANT")){
                event.getOrganizer().setRole("ORGANIZER");
            }
            eventRepository.save(event);
            String message = "Your proposed event " + event.getName() + " has been approved.";
            notificationService.createNotification(event.getOrganizer(), message, event);
            return  new ResponseEntity<>("Event has been approved", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("User is not authorized to approve the event", HttpStatus.UNAUTHORIZED);
        }
    }

    //This is accessed by admin or venue_manager when rejecting a proposed event
    public ResponseEntity<String> rejectEvent(Long userId, User user){
        Optional<Event> eventOptional = eventRepository.findById(userId);

        if(eventOptional.isEmpty()){
            return new ResponseEntity<>("Event does not exist", HttpStatus.NOT_FOUND);
        }
        Event event = eventOptional.get();
        if((user.getRole().equals("ORGANIZER") && user.getUserType().equals("VENUE_MANAGER")) || user.getRole().equals("ADMIN")){
            event.setStatus("Rejected");
            String message = "Your proposed event " + event.getName() + " has been rejected.";
            notificationService.createNotification(event.getOrganizer(), message, event);
            eventRepository.save(event);
            return  new ResponseEntity<>("Event has been approved", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("User is not authorized to approve the event", HttpStatus.UNAUTHORIZED);
        }
    }

    //If the organizer wants to cancel the event
    public ResponseEntity<String> cancelEvent(Long eventId, User user){
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if(eventOpt.isPresent()){
            Event event = eventOpt.get();

            //if user is the organizer or admin, allow to delete.
            if(user.getRole().equals("ADMIN") || event.getOrganizer().getUserID().equals(user.getUserID())){
                List<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId);
                List<User> registeredUsers = registrations.stream().map(EventRegistration::getUser).toList();
                String message = "Sorry, the event " + event.getName() + " has been canceled";
                if(user.getRole().equals("ORGANIZER")){
                event.getOrganizer().setRole("PARTICIPANT");
                }
                notificationService.sendNotificationToUser(registeredUsers, message, event);
                event.setStatus("Canceled");
                return new ResponseEntity<>("Event has been cancelled", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("You are not authorized to cancel this event", HttpStatus.FORBIDDEN);
            }
        }else{
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }
    }


}
