package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/admin")
@RestController
public class AdminController {


    @Autowired
    UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        return userService.getAllUsers();
    }


    @PutMapping("/{userId}/update")
    public ResponseEntity<String> updateUser(Long userId, @RequestBody User updatedUser){
        return userService.updateUser(userId, updatedUser);
    }



}
