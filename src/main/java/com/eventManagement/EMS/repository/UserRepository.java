package com.eventManagement.EMS.repository;

import com.eventManagement.EMS.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);
    Optional <User> findByEmail(String email);
    Optional <User> findBySchoolID(String schoolD);

    List<User> findByRole(String role);

}
