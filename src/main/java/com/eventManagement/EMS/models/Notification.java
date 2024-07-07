package com.eventManagement.EMS.models;

import jakarta.persistence.*;


@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient", nullable = false)
    private User recipient;

    private String message;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String createdAt;

    public Notification() {
    }

    public Notification(Long id, User recipient, String message, Event event, String createdAt) {
        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.event = event;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
