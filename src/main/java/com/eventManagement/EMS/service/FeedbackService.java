package com.eventManagement.EMS.service;

import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.Feedback;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.EventRepository;
import com.eventManagement.EMS.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    EventRepository eventRepository;
    public ResponseEntity<String> createFeedback(Feedback feedback, Long eventId, User user){
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            return new ResponseEntity<>("Event not found",HttpStatus.NOT_FOUND);
        }
        Event event = eventOptional.get();
        feedback.setEvent(event);
        feedback.setUser(user);
        feedback.setSubmittedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));
        feedbackRepository.save(feedback);
        return new ResponseEntity<>("Feedback has been saved", HttpStatus.CREATED);
    }

    //Displays all feedback of a specific event
    public ResponseEntity<List<Feedback>> getAllEventFeedback(Long eventId){
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Feedback> feedbacks = feedbackRepository.findByEventId(eventId);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    public ResponseEntity<String> editFeedback(Long feedbackId, Feedback updatedFeedback, User user){
        Optional<Feedback> existingFeedback = feedbackRepository.findById(feedbackId);
        if(existingFeedback.isEmpty()){
            return new ResponseEntity<>("Feedback id not found", HttpStatus.NOT_FOUND);
        }
        Feedback feedback = existingFeedback.get();
        // Checks if the logged-in user is the one who wrote the feedback
        if(feedback.getUser().getUserID().equals(user.getUserID())){
            feedback.setComments(updatedFeedback.getComments() != null ? updatedFeedback.getComments() : feedback.getComments());
            if(updatedFeedback.getRate() >= 0 && updatedFeedback != null){
                feedback.setRate(updatedFeedback.getRate());
            }else{
                feedback.setRate(feedback.getRate());
            }
            feedback.setSubmittedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));
            return new ResponseEntity<>("Feedback has been updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not authorized", HttpStatus.NOT_FOUND);
    }


    public ResponseEntity<String> deleteFeedback(Long feedbackId, User user){
        Optional<Feedback> feedbackOptional = feedbackRepository.findById(feedbackId);
        Feedback feedback = feedbackOptional.get();
        feedbackRepository.delete(feedback);
        return new ResponseEntity<>("Feedback has been deleted", HttpStatus.NO_CONTENT);
    }

}
