package com.college.model;

import java.sql.Timestamp;

/**
 * Model class representing uploaded study material or document.
 */
public class FileData {
    private int id;
    private String title;
    private String description;
    private String fileName;       // Unique stored filename on disk
    private String originalName;   // Original uploaded filename
    private long fileSize;         // File size in bytes
    private String fileType;       // File extension or MIME type
    private int teacherId;
    private String teacherName;    // Joined from users table
    private int departmentId;
    private String departmentName; // Joined from departments table
    private Timestamp uploadedAt;

    public FileData() {
    }

    public FileData(String title, String description, String fileName, String originalName, long fileSize, String fileType, int teacherId, int departmentId) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.teacherId = teacherId;
        this.departmentId = departmentId;
    }

    // Helper method to format file size for human-readable display (e.g. 2.4 MB)
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    // Helper to get Bootstrap icon class depending on file extension
    public String getIconClass() {
        String ext = fileType != null ? fileType.toLowerCase() : "";
        if (ext.contains("pdf")) return "bi-file-earmark-pdf-fill text-danger";
        if (ext.contains("doc")) return "bi-file-earmark-word-fill text-primary";
        if (ext.contains("ppt")) return "bi-file-earmark-ppt-fill text-warning";
        if (ext.contains("xls") || ext.contains("csv")) return "bi-file-earmark-excel-fill text-success";
        if (ext.contains("zip") || ext.contains("rar")) return "bi-file-earmark-zip-fill text-dark";
        if (ext.contains("txt")) return "bi-file-earmark-text-fill text-secondary";
        if (ext.contains("jpg") || ext.contains("jpeg") || ext.contains("png") || ext.contains("webp") || ext.contains("gif") || ext.contains("svg") || ext.contains("bmp")) {
            return "bi-file-earmark-image-fill text-info";
        }
        return "bi-file-earmark-fill text-secondary";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public Timestamp getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
