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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VenueService {

    @Autowired
    VenueRepository venueRepository;
    @Autowired
    UserRepository userRepository;

    @Value("${upload.venue.dir}")
    public String uploadDir;

    public ResponseEntity<String> addVenue(VenueDTO venueDTO, MultipartFile imageFile) {

        if(!imageFile.isEmpty()){
            try{
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                if(imageFile.getOriginalFilename() != null){
                    Path filePath = uploadPath.resolve(imageFile.getOriginalFilename());
                    imageFile.transferTo(filePath.toFile());

                    venueDTO.setImagePath(filePath.toString());
                }
            }catch (IOException e){
                return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        Optional<User> userOptional = userRepository.findById(venueDTO.getVenueManagersID());
        if(userOptional.isEmpty()){
            return new ResponseEntity<>("Invalid user id", HttpStatus.NOT_FOUND);
        }
        User foundManager = userOptional.get();
        List<User> managers = new ArrayList<>();
        foundManager.setRole("VENUE_MANAGER");
        managers.add(foundManager);
        Venue venue = new Venue();
        venue.setName(venueDTO.getName());
        venue.setLocation(venueDTO.getLocation());
        venue.setMaxCapacity(venueDTO.getMaxCapacity());
        venue.setImagePath(venueDTO.getImagePath());
        venue.setVenueManagers(managers);
        venueRepository.save(venue);
        return new ResponseEntity<>("Venue has been changed successfully", HttpStatus.CREATED);
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
                foundManager.setRole("PARTICIPANT");
            }
            venue.getVenueManagers().clear();
        }

        venueRepository.delete(venue);
        return new ResponseEntity<>("Venue successfully deleted", HttpStatus.NO_CONTENT);
    }
    @Transactional//Deletes all venues. It won't be deleted if it has active event held in the event
    public ResponseEntity<String> deleteAll(){
        List<User> venueManagers = userRepository.findByRole("VENUE_MANAGER");

        for(User user: venueManagers){
            user.setRole("PARTICIPANT");
        }
        venueRepository.deleteAll();
        return new ResponseEntity<>("All venues has been deleted", HttpStatus.NO_CONTENT);
    }
}
