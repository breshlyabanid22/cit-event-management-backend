package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/admin")
@RestController
public class AdminController {


    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        return userService.getAllUsers();
    }

    @PatchMapping("/users/{userID}")
    public ResponseEntity<String> updateUser(@PathVariable Long userID, @RequestBody User updatedUser){
        return userService.updateUser(userID, updatedUser);
    }
    @DeleteMapping("/users/{userID}/deactivate")
    public ResponseEntity<String> deactivateMyAccount(@PathVariable Long userID){
        User user = userRepository.findById(userID).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userService.deactivateAccount(user);
    }

    @PatchMapping("/users/{userID}/activate")
    public ResponseEntity<String> activateMyAccount(@PathVariable Long userID){
        User user = userRepository.findById(userID).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userService.activateAccount(user);
    }


}
