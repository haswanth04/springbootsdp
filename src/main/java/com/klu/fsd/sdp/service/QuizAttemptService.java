package com.klu.fsd.sdp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.fsd.sdp.model.Answer;
import com.klu.fsd.sdp.model.Option;
import com.klu.fsd.sdp.model.Question;
import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.QuizAttempt;
import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.repository.OptionRepository;
import com.klu.fsd.sdp.repository.QuestionRepository;
import com.klu.fsd.sdp.repository.QuizAttemptRepository;
import com.klu.fsd.sdp.repository.QuizRepository;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    public List<QuizAttempt> getAttemptsByUser(User user) {
        return quizAttemptRepository.findByUser(user);
    }
    
    public List<QuizAttempt> getAttemptsByQuiz(Quiz quiz) {
        return quizAttemptRepository.findByQuiz(quiz);
    }
    
    public Optional<QuizAttempt> getActiveAttempt(User user, Quiz quiz) {
        return quizAttemptRepository.findByUserAndQuizAndCompletedFalse(user, quiz);
    }
    
    public Optional<QuizAttempt> getQuizAttemptById(Long id) {
        return quizAttemptRepository.findById(id);
    }
    
    @Transactional
    public QuizAttempt startQuiz(User user, Quiz quiz) {
        // Check if user already has an active attempt
        Optional<QuizAttempt> existingAttempt = getActiveAttempt(user, quiz);
        if (existingAttempt.isPresent()) {
            return existingAttempt.get();
        }
        
        // Create new attempt
        QuizAttempt attempt = new QuizAttempt(user, quiz);
        return quizAttemptRepository.save(attempt);
    }
    
    @Transactional
    public QuizAttempt submitQuiz(Long attemptId, List<Map<String, Object>> answers) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found with id: " + attemptId));
        
        if (attempt.isCompleted()) {
            throw new RuntimeException("Quiz attempt already completed");
        }
        
        int totalPoints = 0;
        int earnedPoints = 0;
        
        for (Map<String, Object> answerData : answers) {
            Long questionId = Long.valueOf(answerData.get("questionId").toString());
            
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
            
            // Add the question's points to the total
            totalPoints += question.getPoints();
            
            // Handle different answer types (MCQ vs text)
            if (answerData.containsKey("selectedOptionId")) {
                // For MCQ questions
                Long selectedOptionId = Long.valueOf(answerData.get("selectedOptionId").toString());
                
                Option selectedOption = optionRepository.findById(selectedOptionId)
                        .orElseThrow(() -> new RuntimeException("Option not found with id: " + selectedOptionId));
                
                Answer answer = new Answer(attempt, question, selectedOption);
                attempt.addAnswer(answer);
                
                if (selectedOption.isCorrect()) {
                    earnedPoints += question.getPoints();
                }
            } else if (answerData.containsKey("answer")) {
                // For text/essay questions
                // For now, simply store the answer, but don't award points 
                // (would require manual grading)
                String textAnswer = answerData.get("answer").toString();
                
                // Create a dummy option to store the text answer
                // Note: This approach depends on your data model
                Option textOption = new Option();
                textOption.setOptionText(textAnswer);
                textOption.setCorrect(false);
                textOption.setQuestion(question);
                
                // Save the option before using it in an Answer
                textOption = optionRepository.save(textOption);
                
                Answer answer = new Answer(attempt, question, textOption);
                attempt.addAnswer(answer);
                
                // Text answers require manual grading, so no points for now
                // You could add a manual grading feature later
            } else {
                // No valid answer provided
                System.out.println("No valid answer provided for question " + questionId);
            }
        }
        
        // Calculate percentage score (0-100)
        int percentageScore = totalPoints == 0 ? 0 : (int) Math.round((double) earnedPoints / totalPoints * 100);
        
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setScore(percentageScore);
        attempt.setCompleted(true);
        
        return quizAttemptRepository.save(attempt);
    }
} 