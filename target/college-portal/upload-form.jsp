<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = (String) session.getAttribute("role");
    if (!"TEACHER".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
        return;
    }
    String error = (String) request.getAttribute("errorMessage");
    String deptName = (String) session.getAttribute("departmentName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Study Material - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .form-card {
            max-width: 620px;
            margin: 40px auto;
            border-radius: 12px;
            border: none;
        }
    </style>
</head>
<body class="bg-light">

    <!-- Top Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/teacher-dashboard.jsp">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <div class="d-flex align-items-center">
                <a href="${pageContext.request.contextPath}/files" class="btn btn-outline-light btn-sm me-2">
                    <i class="bi bi-arrow-left me-1"></i>Back to Files
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Upload Form Container -->
    <div class="container">
        <div class="card form-card shadow p-4 bg-white">
            <div class="mb-4 text-center">
                <h3 class="fw-bold text-primary mb-1">
                    <i class="bi bi-cloud-arrow-up-fill me-2"></i>Upload Study Material
                </h3>
                <p class="text-muted small">Share documents for department: <strong><%= deptName %></strong></p>
            </div>

            <!-- Error Alert -->
            <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Form -->
            <form action="${pageContext.request.contextPath}/upload" method="POST" enctype="multipart/form-data">
                
                <!-- Document Title -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Document Title</label>
                    <input type="text" name="title" class="form-control" 
                           placeholder="e.g. Unit 3 - Operating System Deadlocks" 
                           maxlength="200" required>
                </div>

                <!-- Description -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Short Description / Remarks (Optional)</label>
                    <textarea name="description" class="form-control" rows="3" 
                              placeholder="e.g. Read sections 3.2 and 3.4 for upcoming assignment."></textarea>
                </div>

                <!-- File Selector -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Select File</label>
                    <input type="file" name="file" class="form-control" 
                           accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt,.csv,.jpg,.jpeg,.png,.webp,.gif,.svg,.zip,.rar" required>
                    <div class="form-text text-muted mt-2">
                        <i class="bi bi-info-circle me-1"></i>
                        <strong>Supported:</strong> PDF, Word, PPT, Excel, Images (JPG, PNG, WEBP, GIF), ZIP, TXT &bull; 
                        <strong>Max Size:</strong> 10 MB
                    </div>
                </div>

                <!-- Submit / Cancel -->
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/files" class="btn btn-outline-secondary px-4">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary px-4 fw-semibold shadow-sm">
                        <i class="bi bi-upload me-1"></i>Upload & Share
                    </button>
                </div>

            </form>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
