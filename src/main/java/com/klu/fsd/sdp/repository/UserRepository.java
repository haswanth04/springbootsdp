package com.klu.fsd.sdp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.model.User.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByEmail(String email);
    List<User> findByAssignedExaminer(User examiner);
} 