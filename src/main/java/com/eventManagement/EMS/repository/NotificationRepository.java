package com.eventManagement.EMS.repository;

import com.eventManagement.EMS.models.Notification;
import com.eventManagement.EMS.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(User recipient);
}
