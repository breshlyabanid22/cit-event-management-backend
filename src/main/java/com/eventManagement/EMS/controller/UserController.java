package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.DTO.UserDTO;
import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user){
        if(user.getRole() == null){
            user.setRole("PARTICIPANT");
        }
        return  userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO, HttpServletRequest request){
        return userService.login(userDTO.getUsername(), userDTO.getPassword(), request);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @PatchMapping("/account")//User updates their user profile or account
    public ResponseEntity<String> updateMyProfile(MultipartFile multipartFile, @AuthenticationPrincipal UserDetails userDetails, @RequestBody User updatedUser) {
        Long userId = getUserIdFromUserDetails(userDetails);
        return userService.updateProfile(multipartFile, userId, updatedUser);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserInfoDetails) {
            return ((UserInfoDetails) userDetails).getId();
        }
        throw new IllegalArgumentException("Invalid user details");
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getCurrentUser(@PathVariable Long userId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        User user = userInfoDetails.getUser();
        return userService.getCurrentUser(userId);
    }

    @DeleteMapping("/account/deactivate")
    public ResponseEntity<String> deactivateMyAccount(@AuthenticationPrincipal UserInfoDetails userInfoDetails){
        User user = userInfoDetails.getUser();
        return userService.deactivateAccount(user);
    }


}
