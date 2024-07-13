package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.UserRepository;
import com.eventManagement.EMS.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;



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
    public ResponseEntity<String> login(@RequestBody User user, HttpServletRequest request){
        return userService.login(user.getUsername(), user.getPassword(), request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @PutMapping("/account")//User updates their user profile or account
    public ResponseEntity<String> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody User updatedUser) {
        Long userId = getUserIdFromUserDetails(userDetails);
        return userService.updateProfile(userId, updatedUser);
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserInfoDetails) {
            return ((UserInfoDetails) userDetails).getId();
        }
        throw new IllegalArgumentException("Invalid user details");
    }


    @DeleteMapping("/account/deactivate")
    public ResponseEntity<String> deactivateMyAccount(@AuthenticationPrincipal UserInfoDetails userInfoDetails){
        User user = userInfoDetails.getUser();
        return userService.deactivateAccount(user);
    }


}
