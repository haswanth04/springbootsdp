package com.klu.fsd.sdp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.model.User.Role;
import com.klu.fsd.sdp.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public User createUser(User user) {
        // Store the password as-is (not encoded)
        return userRepository.save(user);
    }
    
    @Transactional
    public User createUser(String name, String email, String password, Role role) {
        User user = new User(name, email, password, role);
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUserStatus(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setActive(active);
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @Transactional
    public User assignExaminer(Long userId, Long examinerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        User examiner = userRepository.findById(examinerId)
                .orElseThrow(() -> new RuntimeException("Examiner not found with id: " + examinerId));
        
        // Check if the examiner actually has the EXAMINER role
        if (examiner.getRole() != Role.EXAMINER) {
            throw new IllegalArgumentException("Assigned user must have EXAMINER role");
        }
        
        user.setAssignedExaminer(examiner);
        return userRepository.save(user);
    }
    
    public List<User> getUsersByAssignedExaminer(User examiner) {
        return userRepository.findByAssignedExaminer(examiner);
    }
    
    public boolean hasExaminerAssigned(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getAssignedExaminer() != null)
                .orElse(false);
    }
    
    @Transactional
    public User approveExaminer(Long examinerId) {
        User examiner = userRepository.findById(examinerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + examinerId));
        
        if (examiner.getRole() != Role.EXAMINER) {
            throw new IllegalArgumentException("User is not an examiner");
        }
        
        examiner.setApprovalStatus(User.ApprovalStatus.APPROVED);
        examiner.setActive(true); // Activate the account
        return userRepository.save(examiner);
    }
    
    @Transactional
    public User rejectExaminer(Long examinerId) {
        User examiner = userRepository.findById(examinerId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + examinerId));
        
        if (examiner.getRole() != Role.EXAMINER) {
            throw new IllegalArgumentException("User is not an examiner");
        }
        
        examiner.setApprovalStatus(User.ApprovalStatus.REJECTED);
        examiner.setActive(false); // Keep the account inactive
        return userRepository.save(examiner);
    }
    
    public List<User> getPendingExaminers() {
        List<User> examiners = userRepository.findByRole(Role.EXAMINER);
        return examiners.stream()
                .filter(examiner -> examiner.getApprovalStatus() == User.ApprovalStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }
} 