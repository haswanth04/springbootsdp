package com.klu.fsd.sdp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.QuizAttempt;
import com.klu.fsd.sdp.model.User;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUser(User user);
    List<QuizAttempt> findByQuiz(Quiz quiz);
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);
    Optional<QuizAttempt> findByUserAndQuizAndCompletedFalse(User user, Quiz quiz);
} 