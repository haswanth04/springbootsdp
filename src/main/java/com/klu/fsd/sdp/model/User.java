package com.klu.fsd.sdp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @ManyToOne
    @JoinColumn(name = "assigned_examiner_id")
    private User assignedExaminer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum Role {
        ADMIN, EXAMINER, USER
    }
    
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
    
    // Set createdAt before persisting
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor
    public User() {}
    
    // Constructor with fields
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        
        // Auto-set approval status for examiners to PENDING
        if (role == Role.EXAMINER) {
            this.approvalStatus = ApprovalStatus.PENDING;
            this.active = false; // Examiners start as inactive until approved
        } else {
            this.approvalStatus = null; // Non-examiners don't need approval status
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public User getAssignedExaminer() {
        return assignedExaminer;
    }
    
    public void setAssignedExaminer(User assignedExaminer) {
        this.assignedExaminer = assignedExaminer;
    }
    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }
    
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
} 