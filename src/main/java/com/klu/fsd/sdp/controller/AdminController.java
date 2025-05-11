package com.klu.fsd.sdp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.model.User.Role;
import com.klu.fsd.sdp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
        System.out.println("Admin requesting all users");
        
        List<User> users = userService.getAllUsers();
        
        List<Map<String, Object>> response = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole().name());
                    userMap.put("active", user.isActive());
                    
                    // Include assigned examiner if present
                    if (user.getAssignedExaminer() != null) {
                        userMap.put("assignedExaminer", Map.of(
                                "id", user.getAssignedExaminer().getId(),
                                "name", user.getAssignedExaminer().getName()
                        ));
                    }
                    
                    return userMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, 
                                             @RequestBody Map<String, Boolean> request) {
        Boolean active = request.get("active");
        if (active == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Active status is required"));
        }
        
        try {
            User user = userService.updateUserStatus(userId, active);
            return ResponseEntity.ok(Map.of("message", "User status updated successfully",
                                          "userId", user.getId(),
                                          "active", user.isActive()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/examiners")
    public ResponseEntity<?> getAllExaminers() {
        List<User> examiners = userService.getUsersByRole(Role.EXAMINER);
        
        List<Map<String, Object>> response = examiners.stream()
                .map(examiner -> {
                    Map<String, Object> examinerMap = new HashMap<>();
                    examinerMap.put("id", examiner.getId());
                    examinerMap.put("name", examiner.getName());
                    examinerMap.put("email", examiner.getEmail());
                    examinerMap.put("active", examiner.isActive());
                    return examinerMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/users/{userId}/assign-examiner")
    public ResponseEntity<?> assignExaminer(@PathVariable Long userId, 
                                           @RequestBody Map<String, Long> request) {
        Long examinerId = request.get("examinerId");
        if (examinerId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Examiner ID is required"));
        }
        
        try {
            User user = userService.assignExaminer(userId, examinerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Examiner assigned successfully");
            response.put("userId", user.getId());
            response.put("assignedExaminer", Map.of(
                    "id", user.getAssignedExaminer().getId(),
                    "name", user.getAssignedExaminer().getName()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/pending-examiners")
    public ResponseEntity<?> getPendingExaminers() {
        List<User> pendingExaminers = userService.getPendingExaminers();
        
        List<Map<String, Object>> response = pendingExaminers.stream()
                .map(examiner -> {
                    Map<String, Object> examinerMap = new HashMap<>();
                    examinerMap.put("id", examiner.getId());
                    examinerMap.put("name", examiner.getName());
                    examinerMap.put("email", examiner.getEmail());
                    examinerMap.put("registeredAt", examiner.getCreatedAt()); // Assuming you have a createdAt field
                    return examinerMap;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/examiners/{examinerId}/approve")
    public ResponseEntity<?> approveExaminer(@PathVariable Long examinerId) {
        try {
            User examiner = userService.approveExaminer(examinerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Examiner approved successfully");
            response.put("examinerId", examiner.getId());
            response.put("name", examiner.getName());
            response.put("status", examiner.getApprovalStatus().name());
            response.put("active", examiner.isActive());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/examiners/{examinerId}/reject")
    public ResponseEntity<?> rejectExaminer(@PathVariable Long examinerId) {
        try {
            User examiner = userService.rejectExaminer(examinerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Examiner rejected successfully");
            response.put("examinerId", examiner.getId());
            response.put("name", examiner.getName());
            response.put("status", examiner.getApprovalStatus().name());
            response.put("active", examiner.isActive());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
} 