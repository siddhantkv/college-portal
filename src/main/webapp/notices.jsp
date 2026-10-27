<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.college.model.Notice" %>
<%
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
    String deptName = (String) session.getAttribute("departmentName");
    List<Notice> notices = (List<Notice>) request.getAttribute("notices");
    String status = request.getParameter("status");

    // Dynamic dashboard link depending on role
    String dashboardUrl = "TEACHER".equalsIgnoreCase(role) ? "teacher-dashboard.jsp" : "student-dashboard.jsp";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Notices - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .notice-card {
            border-left: 5px solid #0d6efd;
            border-radius: 8px;
            transition: transform 0.15s ease;
        }
        .notice-card:hover {
            transform: translateY(-2px);
        }
    </style>
</head>
<body class="bg-light">

    <!-- Top Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark <%= "TEACHER".equalsIgnoreCase(role) ? "bg-primary" : "bg-success" %> shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/<%= dashboardUrl %>">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#noticesNav" aria-controls="noticesNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse mt-2 mt-lg-0" id="noticesNav">
                <div class="ms-auto d-flex flex-column flex-lg-row align-items-lg-center gap-2">
                    <span class="badge bg-light text-dark px-3 py-2">
                        <i class="bi bi-building me-1"></i>Dept: <strong><%= deptName %></strong>
                    </span>
                    <a href="${pageContext.request.contextPath}/<%= dashboardUrl %>" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-speedometer2 me-1"></i>Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-box-arrow-right me-1"></i>Logout
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container my-5">
        
        <!-- Header & Action Button -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <h2 class="fw-bold text-dark mb-0">Department Notices</h2>
                <p class="text-muted mb-0">Official circulars and announcements for <strong><%= deptName %></strong></p>
            </div>
            <% if ("TEACHER".equalsIgnoreCase(role)) { %>
                <a href="${pageContext.request.contextPath}/notices?action=add" class="btn btn-primary px-3 py-2 fw-semibold shadow-sm">
                    <i class="bi bi-plus-circle-fill me-1"></i>Post New Notice
                </a>
            <% } %>
        </div>

        <!-- Status Alerts -->
        <% if ("created".equalsIgnoreCase(status)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>Notice published successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("updated".equalsIgnoreCase(status)) { %>
            <div class="alert alert-info alert-dismissible fade show" role="alert">
                <i class="bi bi-info-circle-fill me-2"></i>Notice updated successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("deleted".equalsIgnoreCase(status)) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-trash-fill me-2"></i>Notice deleted successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Notices List -->
        <% if (notices != null && !notices.isEmpty()) { %>
            <div class="d-flex flex-column gap-3">
                <% for (Notice n : notices) { 
                    boolean isMyNotice = "TEACHER".equalsIgnoreCase(role) && userId != null && userId == n.getTeacherId();
                %>
                    <div class="card notice-card shadow-sm p-4 bg-white border-0">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                            <h4 class="fw-bold text-dark mb-0"><%= n.getTitle() %></h4>
                            <div class="d-flex align-items-center gap-2">
                                <span class="badge bg-secondary-subtle text-secondary border px-2 py-1">
                                    <i class="bi bi-clock me-1"></i><%= n.getCreatedAt() != null ? n.getCreatedAt().toString().substring(0, 16) : "" %>
                                </span>
                                <% if (isMyNotice) { %>
                                    <a href="${pageContext.request.contextPath}/notices?action=edit&id=<%= n.getId() %>" 
                                       class="btn btn-outline-primary btn-sm py-0 px-2" title="Edit Notice">
                                        <i class="bi bi-pencil-square"></i> Edit
                                    </a>
                                    <a href="${pageContext.request.contextPath}/notices?action=delete&id=<%= n.getId() %>" 
                                       class="btn btn-outline-danger btn-sm py-0 px-2" title="Delete Notice"
                                       onclick="return confirm('Are you sure you want to delete this notice?');">
                                        <i class="bi bi-trash"></i> Delete
                                    </a>
                                <% } %>
                            </div>
                        <p class="text-secondary mb-3" style="white-space: pre-wrap;"><%= n.getContent() %></p>

                        <% if (n.hasAttachment()) { %>
                            <div class="mb-3 p-3 bg-light rounded border">
                                <% if (n.isImageAttachment()) { %>
                                    <div class="mb-2">
                                        <a href="${pageContext.request.contextPath}/download?type=notice&id=<%= n.getId() %>&view=inline" target="_blank">
                                            <img src="${pageContext.request.contextPath}/download?type=notice&id=<%= n.getId() %>&view=inline" 
                                                 alt="Notice Image" class="img-fluid rounded border shadow-sm" 
                                                 style="max-height: 380px; object-fit: contain;">
                                        </a>
                                    </div>
                                <% } %>
                                <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
                                    <span class="small text-muted">
                                        <i class="bi bi-paperclip me-1"></i>Attachment: <strong><%= n.getOriginalAttachmentName() %></strong>
                                    </span>
                                    <a href="${pageContext.request.contextPath}/download?type=notice&id=<%= n.getId() %>" 
                                       class="btn btn-outline-primary btn-sm">
                                        <i class="bi bi-download me-1"></i>Download Attachment
                                    </a>
                                </div>
                            </div>
                        <% } %>

                        <div class="small text-muted border-top pt-2">
                            <i class="bi bi-person-fill text-primary me-1"></i>Posted by: <strong><%= n.getTeacherName() %></strong>
                            <span class="mx-2">•</span>
                            <i class="bi bi-building text-secondary me-1"></i><%= n.getDepartmentName() %>
                        </div>
                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="card shadow-sm border-0 text-center py-5 bg-white">
                <i class="bi bi-bell-slash text-muted display-4"></i>
                <h5 class="fw-semibold mt-3 text-secondary">No notices posted yet!</h5>
                <p class="text-muted small">When teachers publish circulars for your department, they will appear here.</p>
                <% if ("TEACHER".equalsIgnoreCase(role)) { %>
                    <div class="mt-2">
                        <a href="${pageContext.request.contextPath}/notices?action=add" class="btn btn-primary btn-sm">
                            <i class="bi bi-plus-lg me-1"></i>Post First Notice
                        </a>
                    </div>
                <% } %>
            </div>
        <% } %>

    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
