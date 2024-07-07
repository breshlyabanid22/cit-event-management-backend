package com.eventManagement.EMS.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userID;

    public Long getUserID() {
        return userID;
    }

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true, name = "school_id")
    private String schoolID;

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_type")
    private String userType;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'USER'")
    private String role;

    private String year;
    private String course;
    private String department;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> organizedEvents;

    @OneToMany(mappedBy = "venueManager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Venue> managedVenues;


    public User(){}

    public User(Long userID,
                String username,
                String password,
                String email,
                String schoolID,
                String firstName,
                String lastName,
                String userType,
                String role,
                String year,
                String course,
                String department,
                List<Event> organizedEvents,
                List<Venue> managedVenues,
                String createdAt,
                String updatedAt) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.schoolID = schoolID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.role = role;
        this.year = year;
        this.course = course;
        this.department = department;
        this.managedVenues = managedVenues;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public List<Venue> getManagedVenues() {
        return managedVenues;
    }

    public void setManagedVenues(List<Venue> managedVenues) {
        this.managedVenues = managedVenues;
    }
}
