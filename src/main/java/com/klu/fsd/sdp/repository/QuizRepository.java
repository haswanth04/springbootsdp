package com.klu.fsd.sdp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.User;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByExaminer(User examiner);
    List<Quiz> findByActiveTrue();
    List<Quiz> findByExaminerAndActiveTrue(User examiner);
    Optional<Quiz> findByTitle(String title);
} 