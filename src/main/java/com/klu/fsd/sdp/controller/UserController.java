package com.klu.fsd.sdp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.fsd.sdp.model.Option;
import com.klu.fsd.sdp.model.Question;
import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.QuizAttempt;
import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.service.QuizAttemptService;
import com.klu.fsd.sdp.service.QuizService;
import com.klu.fsd.sdp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private QuizService quizService;
    
    @Autowired
    private QuizAttemptService quizAttemptService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/quizzes")
    public ResponseEntity<?> getAvailableQuizzes(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        System.out.println("User " + email + " requesting available quizzes");
        
        User user = userService.getUserByEmail(email).orElseThrow();
        System.out.println("Found user: ID=" + user.getId() + ", Name=" + user.getName());
        
        List<Quiz> quizzes;
        
        // If user has an assigned examiner, only show quizzes from that examiner
        if (user.getAssignedExaminer() != null) {
            User examiner = user.getAssignedExaminer();
            System.out.println("User has assigned examiner: " + examiner.getName() + " (ID: " + examiner.getId() + ")");
            quizzes = quizService.getActiveQuizzesByExaminer(examiner);
            System.out.println("Found " + quizzes.size() + " active quizzes from assigned examiner");
        } else {
            // If no assigned examiner, show all active quizzes
            System.out.println("User has no assigned examiner, showing all active quizzes");
            quizzes = quizService.getActiveQuizzes();
            System.out.println("Found " + quizzes.size() + " active quizzes");
        }
        
        // Debug all quizzes
        quizzes.forEach(quiz -> {
            System.out.println("Quiz: ID=" + quiz.getId() + 
                ", Title=" + quiz.getTitle() + 
                ", Active=" + quiz.isActive() + 
                ", Questions=" + quiz.getQuestions().size() +
                ", Examiner=" + (quiz.getExaminer() != null ? quiz.getExaminer().getName() : "null"));
        });
        
        List<Map<String, Object>> response = quizzes.stream()
                .map(quiz -> {
                    Map<String, Object> quizMap = new HashMap<>();
                    quizMap.put("id", quiz.getId());
                    quizMap.put("title", quiz.getTitle());
                    quizMap.put("description", quiz.getDescription());
                    quizMap.put("timeLimit", quiz.getTimeLimit());
                    quizMap.put("createdAt", quiz.getCreatedAt());
                    quizMap.put("questionCount", quiz.getQuestions().size());
                    quizMap.put("examiner", quiz.getExaminer().getName());
                    return quizMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<?> getQuizDetails(@PathVariable Long quizId, HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        System.out.println("User " + email + " requesting details for quiz ID: " + quizId);
        
        User user = userService.getUserByEmail(email).orElseThrow();
        System.out.println("Found user: ID=" + user.getId() + ", Name=" + user.getName());
        
        Quiz quiz = quizService.getQuizById(quizId).orElseThrow();
        System.out.println("Found quiz: ID=" + quiz.getId() + ", Title=" + quiz.getTitle() + ", Active=" + quiz.isActive());
        
        if (!quiz.isActive()) {
            System.out.println("Quiz is not active, denying access");
            return ResponseEntity.status(403).body(Map.of("message", "Quiz is not active"));
        }
        
        // Check if user already has a completed attempt
        boolean hasCompleted = quizAttemptService.getAttemptsByUser(user)
                .stream()
                .filter(attempt -> attempt.getQuiz().getId().equals(quizId))
                .anyMatch(QuizAttempt::isCompleted);
        
        if (hasCompleted) {
            System.out.println("User has already completed this quiz, denying access");
            return ResponseEntity.status(403).body(Map.of("message", "You have already completed this quiz"));
        }
        
        // Get or create attempt
        QuizAttempt attempt = quizAttemptService.startQuiz(user, quiz);
        System.out.println("Started or retrieved attempt: ID=" + attempt.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", quiz.getId());
        response.put("title", quiz.getTitle());
        response.put("description", quiz.getDescription());
        response.put("timeLimit", quiz.getTimeLimit());
        response.put("attemptId", attempt.getId());
        response.put("startedAt", attempt.getStartedAt());
        
        List<Map<String, Object>> questionsData = new ArrayList<>();
        for (Question question : quiz.getQuestions()) {
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("id", question.getId());
            questionData.put("questionText", question.getQuestionText());
            questionData.put("points", question.getPoints());
            
            List<Map<String, Object>> optionsData = new ArrayList<>();
            for (Option option : question.getOptions()) {
                Map<String, Object> optionData = new HashMap<>();
                optionData.put("id", option.getId());
                optionData.put("optionText", option.getOptionText());
                // Don't include isCorrect in the response
                optionsData.add(optionData);
            }
            
            questionData.put("options", optionsData);
            questionsData.add(questionData);
        }
        
        response.put("questions", questionsData);
        System.out.println("Returning quiz details with " + questionsData.size() + " questions");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/submit-quiz/{quizId}")
    public ResponseEntity<?> submitQuiz(@PathVariable Long quizId, @RequestBody Map<String, Object> request,
                                       HttpServletRequest httpRequest) {
        String email = (String) httpRequest.getAttribute("userEmail");
        System.out.println("User " + email + " submitting quiz ID: " + quizId);
        
        User user = userService.getUserByEmail(email).orElseThrow();
        
        Quiz quiz = quizService.getQuizById(quizId).orElseThrow();
        
        List<Map<String, Object>> answers;
        
        // Check if answers is directly a List or inside a Map
        if (request.get("answers") instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> directAnswers = (List<Map<String, Object>>) request.get("answers");
            answers = directAnswers;
        } else {
            // If answers field contains a Map with answers field inside it 
            // (which is the case causing the ClassCastException)
            System.out.println("Received answers in unexpected format, attempting to extract...");
            
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> answersMap = (Map<String, Object>) request.get("answers");
                
                if (answersMap != null && answersMap.get("answers") instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> nestedAnswers = (List<Map<String, Object>>) answersMap.get("answers");
                    answers = nestedAnswers;
                } else {
                    // If we still can't find a valid list of answers, create an empty list
                    System.out.println("Could not find a valid list of answers in the request");
                    answers = new ArrayList<>();
                }
            } catch (ClassCastException e) {
                System.out.println("Error parsing answers: " + e.getMessage());
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid answers format"));
            }
        }
        
        System.out.println("Received " + (answers != null ? answers.size() : 0) + " answers");
        
        QuizAttempt attempt = quizAttemptService.getActiveAttempt(user, quiz)
                .orElseThrow(() -> new RuntimeException("No active attempt found"));
        System.out.println("Found active attempt: ID=" + attempt.getId());
        
        attempt = quizAttemptService.submitQuiz(attempt.getId(), answers);
        System.out.println("Quiz submitted - Score: " + attempt.getScore());
        
        Map<String, Object> response = new HashMap<>();
        response.put("score", attempt.getScore());
        response.put("completedAt", attempt.getCompletedAt());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getUserHistory(HttpServletRequest request) {
        String email = (String) request.getAttribute("userEmail");
        System.out.println("User " + email + " requesting quiz history");
        
        User user = userService.getUserByEmail(email).orElseThrow();
        
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByUser(user);
        System.out.println("Found " + attempts.size() + " attempts, filtering for completed only");
        
        List<Map<String, Object>> response = attempts.stream()
                .filter(QuizAttempt::isCompleted)
                .map(attempt -> {
                    System.out.println("Including attempt: ID=" + attempt.getId() + 
                            ", Quiz=" + attempt.getQuiz().getTitle() + 
                            ", Score=" + attempt.getScore());
                    
                    Map<String, Object> attemptData = new HashMap<>();
                    attemptData.put("id", attempt.getId());
                    attemptData.put("quiz", Map.of(
                            "id", attempt.getQuiz().getId(),
                            "title", attempt.getQuiz().getTitle(),
                            "examiner", attempt.getQuiz().getExaminer().getName()
                    ));
                    attemptData.put("startedAt", attempt.getStartedAt());
                    attemptData.put("completedAt", attempt.getCompletedAt());
                    attemptData.put("score", attempt.getScore());
                    return attemptData;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
} 