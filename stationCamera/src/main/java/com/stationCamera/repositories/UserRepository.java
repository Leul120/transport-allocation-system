package com.stationCamera.repositories;

import com.stationCamera.entities.Role;
import com.stationCamera.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    User findByRole(Role role);
}
