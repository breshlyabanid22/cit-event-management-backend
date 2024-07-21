package com.eventManagement.EMS.service;


import com.eventManagement.EMS.DTO.NotificationDTO;
import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.Notification;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.repository.NotificationRepository;
import com.eventManagement.EMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;
    public Notification createNotification(User recipient, String message, Event event){
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setEvent(event);
        notification.setRecipient(recipient);
        notification.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));
        return notificationRepository.save(notification);
    }
    public Notification regularNotification(User recipient, String message){
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("E MMM dd yyyy")));
        return notificationRepository.save(notification);
    }

    //Fetch all the notifications of the logged-in user
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUser(Long userID){
        Optional<User> userOptional = userRepository.findById(userID);
        if(userOptional.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        List<Notification> notifications = notificationRepository.findByRecipient(user);
        List<NotificationDTO> notificationList = new ArrayList<>();

        for(Notification notification : notifications){
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setId(notification.getId());
            notificationDTO.setRecipient(notification.getRecipient().getUsername());
            notificationDTO.setMessage(notification.getMessage());
            if(notification.getEvent() != null){
                notificationDTO.setEvent(notification.getEvent().getName());
            }
            notificationDTO.setCreatedAt(notification.getCreatedAt());
            notificationList.add(notificationDTO);
        }
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

    //Send notifications to all users
    public void sendNotificationToUser(List<User> users, String message, Event event){
        for(User user: users){
            createNotification(user, message, event);
        }
    }
    public ResponseEntity<String> deleteNotification(Long notificationID){
        notificationRepository.deleteById(notificationID);
        return new ResponseEntity<>("Notif is deleted", HttpStatus.NO_CONTENT);
    }
}
