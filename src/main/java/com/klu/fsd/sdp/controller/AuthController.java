package com.klu.fsd.sdp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.model.User.Role;
import com.klu.fsd.sdp.security.JwtTokenUtil;
import com.klu.fsd.sdp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        System.out.println("Login attempt: " + email);
        
        if (email == null || password == null) {
            System.out.println("Login failed: Email or password is null");
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }
        
        Optional<User> userOpt = userService.getUserByEmail(email);
        
        if (userOpt.isEmpty()) {
            System.out.println("Login failed: User not found with email: " + email);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or password"));
        }
        
        User user = userOpt.get();
        
        if (!user.getPassword().equals(password)) {
            System.out.println("Login failed: Invalid password for user: " + email);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or password"));
        }
        
        // Check examiner approval status
        if (user.getRole() == Role.EXAMINER) {
            if (user.getApprovalStatus() == User.ApprovalStatus.PENDING) {
                System.out.println("Login failed: Examiner account pending approval: " + email);
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Your examiner account is pending approval by an administrator.",
                    "approvalStatus", "PENDING"
                ));
            }
            
            if (user.getApprovalStatus() == User.ApprovalStatus.REJECTED) {
                System.out.println("Login failed: Examiner account rejected: " + email);
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Your examiner account was not approved by the administrator.",
                    "approvalStatus", "REJECTED"
                ));
            }
        }
        
        if (!user.isActive()) {
            System.out.println("Login failed: User account is disabled: " + email);
            return ResponseEntity.badRequest().body(Map.of("message", "User account is disabled"));
        }
        
        final String token = jwtTokenUtil.generateToken(user);
        System.out.println("Login successful: " + email + ", role: " + user.getRole());
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String name = request.get("name");
        String password = request.get("password");
        String roleStr = request.get("role");
        
        System.out.println("Register attempt: " + email + ", name: " + name + ", role: " + roleStr);
        
        if (email == null || name == null || password == null || roleStr == null) {
            System.out.println("Registration failed: Missing required fields");
            return ResponseEntity.badRequest().body(Map.of("message", "Name, email, password and role are required"));
        }
        
        if (userService.emailExists(email)) {
            System.out.println("Registration failed: Email already in use: " + email);
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }
        
        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: Invalid role: " + roleStr);
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid role"));
        }
        
        User user = userService.createUser(name, email, password, role);
        System.out.println("Registration successful: " + email + ", ID: " + user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        
        if (role == Role.EXAMINER) {
            // For examiners, inform them they need admin approval
            response.put("message", "Registration successful. Your account requires admin approval before you can log in.");
            response.put("approvalStatus", user.getApprovalStatus().name());
            response.put("active", user.isActive());
        } else {
            // For regular users and admins
            response.put("message", "User registered successfully");
        }
        
        return ResponseEntity.ok(response);
    }
} 