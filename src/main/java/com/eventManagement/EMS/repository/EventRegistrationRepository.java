package com.eventManagement.EMS.repository;

import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.EventRegistration;
import com.eventManagement.EMS.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    Optional<EventRegistration> findByEventAndUser(Event event, User user);

    List<EventRegistration> findByEventId(Long eventId);
}
