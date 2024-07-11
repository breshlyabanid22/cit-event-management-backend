package com.eventManagement.EMS.repository;


import com.eventManagement.EMS.models.Event;
import com.eventManagement.EMS.models.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.venue.id = :venueId AND " +
            "((e.startTime BETWEEN :startTime AND :endTime) OR " +
            "(e.endTime BETWEEN :startTime AND :endTime) OR " +
            "(e.startTime <= :startTime AND e.endTime >= :endTime))")
    List<Event> findByVenueAndTimeRange(
            @Param("venueId") Long venueId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<Event> findByVenueId(Long venue);
}
