package com.eventManagement.EMS.service;

import com.eventManagement.EMS.DTO.UserDTO;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.models.Venue;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.repository.VenueRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
public class UserService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    NotificationService notificationService;
    @Autowired
    VenueRepository venueRepository;
    @Value("${upload.user.dir}")
    private String uploadDir;

    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        if(users.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else{
            for(User user: users){
                UserDTO userDTO = new UserDTO();
                userDTO.setUserID(user.getUserID());
                userDTO.setUsername(user.getUsername());
                userDTO.setPassword(user.getPassword());
                userDTO.setEmail(user.getEmail());
                userDTO.setRole(user.getRole());
                userDTO.setActive(user.isActive());
                userDTO.setUserType(user.getUserType());
                userDTO.setSchoolID(user.getSchoolID());
                userDTO.setYear(user.getYear());
                userDTO.setCourse(user.getCourse());
                userDTO.setDepartment(user.getDepartment());
                userDTO.setImagePath(user.getImagePath());
                userDTO.setFirstName(user.getFirstName());
                userDTO.setLastName(user.getLastName());
                userDTO.setCreatedAt(user.getCreatedAt());
                userDTO.setUpdatedAt(user.getUpdatedAt());
                userDTOList.add(userDTO);
            }
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        }
    }

    public ResponseEntity<String> register(User user){
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return new ResponseEntity<>("Username already exists", HttpStatus.CONFLICT);
        }else if(userRepository.findByEmail(user.getEmail()).isPresent()){
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }else if(userRepository.findBySchoolID(user.getSchoolID()).isPresent()){
            return new ResponseEntity<>("School ID already exists", HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        user.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, MMM dd yyyy")));
        userRepository.save(user);
        return new ResponseEntity<>("User Registered Successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<UserDTO> login(String username, String password, HttpServletRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setUserID(user.getUserID());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(passwordEncoder.encode(user.getPassword()));
        userDTO.setCourse(user.getCourse());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUserType(user.getUserType());
        userDTO.setRole(user.getRole());
        userDTO.setEmail(user.getEmail());
        userDTO.setActive(user.isActive());
        userDTO.setYear(user.getYear());
        userDTO.setDepartment(user.getDepartment());
        userDTO.setCreatedAt(user.getCreatedAt());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            if (authentication != null) {
                // Store authentication in the session
                SecurityContextHolder.getContext().setAuthentication(authentication);
                HttpSession session = request.getSession(true); // true to create a new session if it doesn't exist
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    //Profile Management for users
    public ResponseEntity<String> updateProfile(MultipartFile imageFile, Long userId, User updatedUser) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if(updatedUser.getUsername() != null && !updatedUser.getUsername().equals(" ")){
                //Checks if the updated username is equal to the existing data in the database
                if(updatedUser.getUsername().equals(existingUser.getUsername())){
                    existingUser.setUsername(updatedUser.getUsername());
                }else{
                    existingUser.setUsername(existingUser.getUsername());
                }
            }
            existingUser.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : existingUser.getEmail());
            existingUser.setYear(updatedUser.getYear() != null ? updatedUser.getYear() : existingUser.getYear());
            existingUser.setCourse(updatedUser.getCourse() != null ? updatedUser.getCourse() : existingUser.getCourse());
            existingUser.setDepartment(updatedUser.getDepartment() != null ? updatedUser.getDepartment() : existingUser.getDepartment());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().equals(" ")) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            if(imageFile != null && !imageFile.isEmpty()){
                try {
                    Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                    Files.createDirectories(uploadPath);
                    if(imageFile.getOriginalFilename() != null){
                        Path filePath = uploadPath.resolve(imageFile.getOriginalFilename());
                        imageFile.transferTo(filePath.toFile());

                        existingUser.setImagePath(filePath.toString());
                    }
                }catch (IOException e){
                    return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            existingUser.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));
            userRepository.save(existingUser);
            String message = "Your profile has been updated!";
            notificationService.regularNotification(existingUser, message);
            return new ResponseEntity<>("User profile updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }
    public ResponseEntity<String> updateUsername(Long userId, String updatedUsername){
        Optional<User> findUser = userRepository.findById(userId);
        if(findUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        //Checks if username is already taken
        Optional<User> existingUserWithSameUsername = userRepository.findByUsername(updatedUsername);
        if(existingUserWithSameUsername.isPresent()){
            return new ResponseEntity<>("Username already taken.", HttpStatus.CONFLICT);
        }
        User existingUser = findUser.get();
        existingUser.setUsername(updatedUsername);
        userRepository.save(existingUser);
        String message = "Your username has been updated!";
        notificationService.regularNotification(existingUser, message);
        return new ResponseEntity<>("Username has been saved", HttpStatus.OK);
    }
    public ResponseEntity<String> updatePassword(Long userId, String updatedPassword){
        Optional<User> findUser = userRepository.findById(userId);
        if(findUser.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User existingUser = findUser.get();
        existingUser.setPassword(passwordEncoder.encode(updatedPassword));
        userRepository.save(existingUser);
        String message = "Your password has been updated!";
        notificationService.regularNotification(existingUser, message);
        return new ResponseEntity<>("Password has been saved", HttpStatus.OK);
    }

    //Admin can update users' profile
    public ResponseEntity<String> updateUser(Long userId, UserDTO updatedUser) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            if(updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()){
                //Checks if the updated username is equal to the existing data in the database
                if(updatedUser.getUsername().equals(existingUser.getUsername())){
                    existingUser.setUsername(existingUser.getUsername());
                }else{
                    existingUser.setUsername(updatedUser.getUsername());
                }
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
                if (userRepository.existsByEmail(updatedUser.getEmail())) {
                    return new ResponseEntity<>("Email already in use", HttpStatus.CONFLICT);
                }
            }
            if(updatedUser.getSchoolID() != null && !updatedUser.getSchoolID().equals(existingUser.getSchoolID())){
                if(userRepository.existsBySchoolID(updatedUser.getSchoolID())){
                    return new ResponseEntity<>("School ID already exist", HttpStatus.CONFLICT);
                }
            }
            existingUser.setEmail(updatedUser.getEmail() != null ? updatedUser.getEmail() : existingUser.getEmail());
            existingUser.setSchoolID(updatedUser.getSchoolID() != null  ? updatedUser.getSchoolID() : existingUser.getSchoolID());
            existingUser.setFirstName(updatedUser.getFirstName() != null ? updatedUser.getFirstName() : existingUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName() != null ? updatedUser.getLastName() : existingUser.getLastName());
            existingUser.setUserType(updatedUser.getUserType() != null ? updatedUser.getUserType() : existingUser.getUserType());
            existingUser.setYear(updatedUser.getYear() != null ? updatedUser.getYear() : existingUser.getYear());
            existingUser.setCourse(updatedUser.getCourse() != null ? updatedUser.getCourse() : existingUser.getCourse());
            existingUser.setDepartment(updatedUser.getDepartment() != null ? updatedUser.getDepartment() : existingUser.getDepartment());
            existingUser.setRole(updatedUser.getRole() != null ? updatedUser.getRole() : existingUser.getRole() );
            existingUser.setSchoolID(updatedUser.getSchoolID() != null ? updatedUser.getSchoolID() : existingUser.getSchoolID());

            List<Long> managedVenuesIDs = updatedUser.getManagedVenuesID();
            if(managedVenuesIDs != null){
                List<Venue> managedVenues = managedVenuesIDs.stream()
                        .map(id -> venueRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid venue ID")))
                        .toList();
                existingUser.setManagedVenues(managedVenues);
                existingUser.setUserType("VENUE_MANAGER");
            }else{
                existingUser.setManagedVenues(existingUser.getManagedVenues());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            existingUser.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E, MMM dd yyyy")));
            userRepository.save(existingUser);
            return new ResponseEntity<>("User has been updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<UserDTO> getCurrentUser(Long userId){
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO();
            userDTO.setUserID(user.getUserID());
            userDTO.setUsername(user.getUsername());
            userDTO.setPassword(user.getPassword());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setRole(user.getRole());
            userDTO.setUserType(user.getUserType());
            userDTO.setEmail(user.getEmail());
            userDTO.setCourse(user.getCourse());
            userDTO.setDepartment(user.getDepartment());
            userDTO.setSchoolID(user.getSchoolID());
            userDTO.setActive(user.isActive());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setUpdatedAt(user.getUpdatedAt());
            userDTO.setYear(user.getYear());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public ResponseEntity<String> deactivateAccount(User user){
        user.setActive(false);
        userRepository.save(user);
        return new ResponseEntity<>("Account deactivated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> activateAccount(User user){
        user.setActive(true);
        userRepository.save(user);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }


}
