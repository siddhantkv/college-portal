<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.college.model.FileData" %>
<%
    String role = (String) session.getAttribute("role");
    Integer userId = (Integer) session.getAttribute("userId");
    String deptName = (String) session.getAttribute("departmentName");
    List<FileData> files = (List<FileData>) request.getAttribute("files");
    String status = request.getParameter("status");

    // Dynamic dashboard URL
    String dashboardUrl = "TEACHER".equalsIgnoreCase(role) ? "teacher-dashboard.jsp" : "student-dashboard.jsp";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Study Materials & Documents - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <!-- Top Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark <%= "TEACHER".equalsIgnoreCase(role) ? "bg-primary" : "bg-success" %> shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/<%= dashboardUrl %>">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#filesNav" aria-controls="filesNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse mt-2 mt-lg-0" id="filesNav">
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
                <h2 class="fw-bold text-dark mb-0">Study Material & Notes</h2>
                <p class="text-muted mb-0">Course documents, lab manuals, and slides for <strong><%= deptName %></strong></p>
            </div>
            <% if ("TEACHER".equalsIgnoreCase(role)) { %>
                <a href="${pageContext.request.contextPath}/upload" class="btn btn-primary px-3 py-2 fw-semibold shadow-sm">
                    <i class="bi bi-cloud-arrow-up-fill me-1"></i>Upload Document
                </a>
            <% } %>
        </div>

        <!-- Status Alerts -->
        <% if ("uploaded".equalsIgnoreCase(status)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>File uploaded and shared with students successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("deleted".equalsIgnoreCase(status)) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-trash-fill me-2"></i>File removed successfully.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Files List Table -->
        <div class="card shadow-sm border-0 bg-white">
            <div class="card-body p-0">
                <% if (files != null && !files.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Document</th>
                                    <th>Description</th>
                                    <th>Uploaded By</th>
                                    <th>Size</th>
                                    <th>Date</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (FileData f : files) { 
                                    boolean isMyFile = "TEACHER".equalsIgnoreCase(role) && userId != null && userId == f.getTeacherId();
                                %>
                                <tr>
                                    <td>
                                        <div class="d-flex align-items-center">
                                            <i class="bi <%= f.getIconClass() %> fs-2 me-3"></i>
                                            <div>
                                                <h6 class="fw-bold mb-0 text-dark"><%= f.getTitle() %></h6>
                                                <small class="text-muted"><%= f.getOriginalName() %></small>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <small class="text-secondary">
                                            <%= (f.getDescription() != null && !f.getDescription().isEmpty()) ? f.getDescription() : "<em>No description</em>" %>
                                        </small>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark border">
                                            <i class="bi bi-person-fill text-primary me-1"></i><%= f.getTeacherName() %>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-secondary-subtle text-secondary border">
                                            <%= f.getFormattedFileSize() %>
                                        </span>
                                    </td>
                                    <td>
                                        <small class="text-muted">
                                            <%= f.getUploadedAt() != null ? f.getUploadedAt().toString().substring(0, 10) : "" %>
                                        </small>
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group">
                                            <a href="${pageContext.request.contextPath}/download?id=<%= f.getId() %>" 
                                               class="btn btn-outline-success btn-sm px-3" title="Download File">
                                                <i class="bi bi-cloud-arrow-down-fill me-1"></i>Download
                                            </a>
                                            <% if (isMyFile) { %>
                                                <a href="${pageContext.request.contextPath}/upload?action=delete&id=<%= f.getId() %>" 
                                                   class="btn btn-outline-danger btn-sm" title="Delete File"
                                                   onclick="return confirm('Permanently delete this file?');">
                                                    <i class="bi bi-trash"></i>
                                                </a>
                                            <% } %>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center py-5">
                        <i class="bi bi-folder-x text-muted display-4"></i>
                        <h5 class="fw-semibold mt-3 text-secondary">No files uploaded yet!</h5>
                        <p class="text-muted small">Teachers can share PDFs, DOCX notes, and PPT presentations here.</p>
                        <% if ("TEACHER".equalsIgnoreCase(role)) { %>
                            <div class="mt-2">
                                <a href="${pageContext.request.contextPath}/upload" class="btn btn-primary btn-sm">
                                    <i class="bi bi-upload me-1"></i>Upload First Document
                                </a>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </div>
        </div>

    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
