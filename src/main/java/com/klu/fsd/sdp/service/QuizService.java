package com.klu.fsd.sdp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.fsd.sdp.model.Option;
import com.klu.fsd.sdp.model.Question;
import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.repository.OptionRepository;
import com.klu.fsd.sdp.repository.QuestionRepository;
import com.klu.fsd.sdp.repository.QuizRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    
    public List<Quiz> getActiveQuizzes() {
        return quizRepository.findByActiveTrue();
    }
    
    public List<Quiz> getQuizzesByExaminer(User examiner) {
        return quizRepository.findByExaminer(examiner);
    }
    
    public List<Quiz> getActiveQuizzesByExaminer(User examiner) {
        return quizRepository.findByExaminerAndActiveTrue(examiner);
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    @Transactional
    public Question addQuestion(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        
        quiz.addQuestion(question);
        quizRepository.save(quiz);
        
        return question;
    }
    
    @Transactional
    public Option addOption(Long questionId, Option option) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
        
        question.addOption(option);
        questionRepository.save(question);
        
        return option;
    }
    
    @Transactional
    public Quiz updateQuizStatus(Long quizId, boolean active) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        
        quiz.setActive(active);
        return quizRepository.save(quiz);
    }
    
    @Transactional
    public void deleteQuiz(Long quizId) {
        quizRepository.deleteById(quizId);
    }
} 