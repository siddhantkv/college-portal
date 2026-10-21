<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Ensure only logged-in TEACHER can view this page
    String role = (String) session.getAttribute("role");
    if (role == null || !"TEACHER".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Dashboard - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .feature-card {
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            border-radius: 12px;
            border: none;
        }
        .feature-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.08);
        }
    </style>
</head>
<body class="bg-light">

    <!-- Top Teacher Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <div class="d-flex align-items-center">
                <span class="badge bg-light text-primary me-3 px-3 py-2 fs-6">
                    <i class="bi bi-building me-1"></i>Dept: <strong><%= session.getAttribute("departmentName") %></strong>
                </span>
                <span class="text-white me-3">
                    <i class="bi bi-person-circle me-1"></i>Hello, <strong><%= session.getAttribute("userName") %></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container my-5">
        <div class="card shadow-sm border-0 p-4 mb-4 bg-white">
            <h2 class="fw-bold text-dark mb-1">Teacher Dashboard</h2>
            <p class="text-muted">
                Welcome back! Manage notices, share documents, and solve doubts for your department 
                (<span class="fw-semibold text-primary"><%= session.getAttribute("departmentName") %></span>).
            </p>
        </div>

        <!-- Quick Action Cards Grid (Phases 3, 4, 5 preview) -->
        <div class="row g-4">
            
            <!-- Notices Card -->
            <div class="col-md-4">
                <div class="card feature-card shadow-sm h-100 p-4 bg-white">
                    <div class="text-primary mb-3">
                        <i class="bi bi-megaphone-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold">Post Notices</h5>
                    <p class="text-muted small">Publish official circulars and class updates for your department students.</p>
                    <a href="${pageContext.request.contextPath}/notices" class="btn btn-primary btn-sm mt-auto">
                        Manage Notices <i class="bi bi-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>

            <!-- Upload Documents Card -->
            <div class="col-md-4">
                <div class="card feature-card shadow-sm h-100 p-4 bg-white">
                    <div class="text-success mb-3">
                        <i class="bi bi-file-earmark-arrow-up-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold">Upload Study Material</h5>
                    <p class="text-muted small">Share lecture notes, PDFs, PPTs and lab manuals up to 10MB securely.</p>
                    <a href="${pageContext.request.contextPath}/files" class="btn btn-success btn-sm mt-auto">
                        Upload Files <i class="bi bi-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>

            <!-- Student Queries Card -->
            <div class="col-md-4">
                <div class="card feature-card shadow-sm h-100 p-4 bg-white">
                    <div class="text-warning mb-3">
                        <i class="bi bi-chat-left-dots-fill fs-1"></i>
                    </div>
                    <h5 class="fw-bold">Student Doubts & Queries</h5>
                    <p class="text-muted small">Review one-to-one queries asked by students and post your answers.</p>
                    <a href="${pageContext.request.contextPath}/queries" class="btn btn-warning btn-sm mt-auto text-dark fw-semibold">
                        View Inbox <i class="bi bi-arrow-right ms-1"></i>
                    </a>
                </div>
            </div>

        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
