package com.eventManagement.EMS.controller;

import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.Feedback;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {


    @Autowired
    FeedbackService feedbackService;

    @PostMapping("/create/{eventId}")
    public ResponseEntity<String> createFeedback(@RequestBody Feedback feedback, @PathVariable Long eventId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return feedbackService.createFeedback(feedback, eventId, user);
    }

    @GetMapping("/display/{eventId}") //Get all feedback of a specific event then display
    public ResponseEntity<List<Feedback>> getAllEventFeedback(@PathVariable Long eventId, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return feedbackService.getAllEventFeedback(eventId);
    }

    @PutMapping("/edit/{feedbackId}")
    public ResponseEntity<String> editFeedback(Long feedbackId, @RequestBody Feedback updatedFeedback, @AuthenticationPrincipal UserInfoDetails userInfoDetails){
        if(userInfoDetails == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        User user = userInfoDetails.getUser();
        return feedbackService.editFeedback(feedbackId, updatedFeedback, user);
    }
}
