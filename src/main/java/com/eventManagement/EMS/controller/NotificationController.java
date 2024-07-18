package com.eventManagement.EMS.controller;


import com.eventManagement.EMS.DTO.NotificationDTO;
import com.eventManagement.EMS.config.UserInfoDetails;
import com.eventManagement.EMS.models.User;
import com.eventManagement.EMS.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;


    @GetMapping("/{userID}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userID){
        return notificationService.getNotificationsByUser(userID);
    }


}
