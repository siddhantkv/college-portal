package com.college.model;

import java.sql.Timestamp;

/**
 * Model class representing a Department Notice.
 * Notices are posted by Teachers and read by Students of the same department.
 * Supports optional file/image attachments (e.g. Circular photo, PDF datesheet).
 */
public class Notice {
    private int id;
    private String title;
    private String content;
    private int teacherId;
    private String teacherName;          // Joined from users table
    private int departmentId;
    private String departmentName;       // Joined from departments table
    private String attachmentName;       // Stored unique file name on disk (optional)
    private String originalAttachmentName; // Original uploaded file name (optional)
    private Timestamp createdAt;

    public Notice() {
    }

    public Notice(String title, String content, int teacherId, int departmentId) {
        this.title = title;
        this.content = content;
        this.teacherId = teacherId;
        this.departmentId = departmentId;
    }

    public Notice(String title, String content, int teacherId, int departmentId, String attachmentName, String originalAttachmentName) {
        this.title = title;
        this.content = content;
        this.teacherId = teacherId;
        this.departmentId = departmentId;
        this.attachmentName = attachmentName;
        this.originalAttachmentName = originalAttachmentName;
    }

    // Helper to check if notice has an image attachment
    public boolean isImageAttachment() {
        if (attachmentName == null) return false;
        String lower = attachmentName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") 
                || lower.endsWith(".webp") || lower.endsWith(".gif") || lower.endsWith(".svg");
    }

    // Helper to check if notice has an attachment
    public boolean hasAttachment() {
        return attachmentName != null && !attachmentName.trim().isEmpty();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getOriginalAttachmentName() {
        return originalAttachmentName;
    }

    public void setOriginalAttachmentName(String originalAttachmentName) {
        this.originalAttachmentName = originalAttachmentName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
