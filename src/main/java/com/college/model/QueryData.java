package com.college.model;

import java.sql.Timestamp;

/**
 * Model class representing a Student Query / Doubt.
 * Inbox style (not real-time chat).
 * Status: 'PENDING', 'ANSWERED'
 */
public class QueryData {
    private int id;
    private int studentId;
    private String studentName;      // Joined from users table
    private int teacherId;
    private String teacherName;      // Joined from users table
    private int departmentId;
    private String departmentName;   // Joined from departments table
    private String question;
    private String answer;
    private String status;           // PENDING, ANSWERED
    private Timestamp createdAt;
    private Timestamp answeredAt;

    public QueryData() {
    }

    public QueryData(int studentId, int teacherId, int departmentId, String question) {
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.departmentId = departmentId;
        this.question = question;
        this.status = "PENDING";
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isAnswered() {
        return "ANSWERED".equalsIgnoreCase(status);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public Timestamp getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Timestamp answeredAt) {
        this.answeredAt = answeredAt;
    }
}
