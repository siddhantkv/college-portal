<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.college.model.User" %>
<%
    // Ensure only logged-in ADMIN can view this page
    String role = (String) session.getAttribute("role");
    if (role == null || !"ADMIN".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
        return;
    }
    List<User> pendingTeachers = (List<User>) request.getAttribute("pendingTeachers");
    String statusMsg = request.getParameter("status");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <!-- Top Admin Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">
                <i class="bi bi-shield-lock-fill text-warning me-2"></i>Admin Console
            </a>
            <div class="d-flex align-items-center">
                <span class="text-light me-3">
                    <i class="bi bi-person-circle me-1"></i>Welcome, <strong><%= session.getAttribute("userName") %></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger btn-sm">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Content Container -->
    <div class="container my-5">
        
        <!-- Header banner -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold mb-0 text-dark">Teacher Approvals</h2>
                <p class="text-muted">Manage teacher registrations across all college departments</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-outline-primary btn-sm">
                <i class="bi bi-arrow-clockwise me-1"></i>Refresh List
            </a>
        </div>

        <!-- Alert messages -->
        <% if ("approved".equalsIgnoreCase(statusMsg)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>Teacher account has been <strong>approved</strong> successfully! They can now login.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("rejected".equalsIgnoreCase(statusMsg)) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="bi bi-x-circle-fill me-2"></i>Teacher registration request has been <strong>rejected</strong>.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Pending Teachers Table Card -->
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white py-3">
                <h5 class="card-title fw-bold mb-0 text-primary">
                    <i class="bi bi-person-check-fill me-2"></i>Pending Teacher Requests
                </h5>
            </div>
            <div class="card-body p-0">
                <% if (pendingTeachers != null && !pendingTeachers.isEmpty()) { %>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Teacher Name</th>
                                    <th>Email</th>
                                    <th>Department</th>
                                    <th>Applied On</th>
                                    <th>Status</th>
                                    <th class="text-center">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% 
                                int index = 1;
                                for (User teacher : pendingTeachers) { 
                                %>
                                <tr>
                                    <td><%= index++ %></td>
                                    <td class="fw-semibold"><%= teacher.getName() %></td>
                                    <td><%= teacher.getEmail() %></td>
                                    <td><span class="badge bg-info text-dark"><%= teacher.getDepartmentName() %></span></td>
                                    <td><%= teacher.getCreatedAt() != null ? teacher.getCreatedAt().toString().substring(0, 16) : "N/A" %></td>
                                    <td><span class="badge bg-warning text-dark">PENDING</span></td>
                                    <td class="text-center">
                                        <a href="${pageContext.request.contextPath}/admin?action=approve&id=<%= teacher.getId() %>" 
                                           class="btn btn-success btn-sm me-1"
                                           onclick="return confirm('Approve this teacher account?');">
                                            <i class="bi bi-check-lg me-1"></i>Approve
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin?action=reject&id=<%= teacher.getId() %>" 
                                           class="btn btn-danger btn-sm"
                                           onclick="return confirm('Reject this teacher registration?');">
                                            <i class="bi bi-x-lg me-1"></i>Reject
                                        </a>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="text-center py-5">
                        <i class="bi bi-check2-circle text-success display-4"></i>
                        <h5 class="fw-semibold mt-3 text-secondary">No pending teacher accounts!</h5>
                        <p class="text-muted small">All registered teachers have already been verified.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
