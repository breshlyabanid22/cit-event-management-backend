package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {


    @Autowired
    UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        return userService.getAllUsers();
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.update(id, updatedUser);
    }



}
