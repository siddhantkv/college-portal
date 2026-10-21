package com.college.model;

import java.sql.Timestamp;

/**
 * Model class representing a User in the system.
 * Roles: 'ADMIN', 'TEACHER', 'STUDENT'
 * Status: 'PENDING' (for new teachers), 'APPROVED', 'REJECTED'
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;       // BCrypt hashed string
    private String role;           // ADMIN, TEACHER, STUDENT
    private Integer departmentId;  // null for ADMIN
    private String departmentName; // Populated from join for easy display
    private String status;         // PENDING, APPROVED, REJECTED
    private Timestamp createdAt;

    // Default constructor
    public User() {
    }

    // Constructor without id/timestamp (used during registration)
    public User(String name, String email, String password, String role, Integer departmentId, String status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
        this.status = status;
    }

    // Full constructor
    public User(int id, String name, String email, String password, String role, Integer departmentId, String status, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
