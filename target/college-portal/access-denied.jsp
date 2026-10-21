<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String reason = request.getParameter("reason");
    String role = (String) session.getAttribute("role");
    String dashboardUrl = request.getContextPath() + "/login.jsp";
    if ("ADMIN".equalsIgnoreCase(role)) {
        dashboardUrl = request.getContextPath() + "/admin";
    } else if ("TEACHER".equalsIgnoreCase(role)) {
        dashboardUrl = request.getContextPath() + "/teacher-dashboard.jsp";
    } else if ("STUDENT".equalsIgnoreCase(role)) {
        dashboardUrl = request.getContextPath() + "/student-dashboard.jsp";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Access Denied - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .error-card {
            max-width: 520px;
            margin: 100px auto;
            border-radius: 16px;
            border: none;
        }
    </style>
</head>
<body>

    <div class="container">
        <div class="card error-card shadow-lg p-5 text-center bg-white">
            <div class="mb-3">
                <i class="bi bi-shield-slash-fill text-danger display-1"></i>
            </div>
            <h2 class="fw-bold text-dark">Access Denied (403)</h2>
            
            <% if ("department".equalsIgnoreCase(reason)) { %>
                <p class="text-secondary mt-2">
                    <strong>Department Security Wall:</strong> You are not permitted to view, download, or edit 
                    content belonging to a different department.
                </p>
                <div class="alert alert-warning py-2 small">
                    <i class="bi bi-lock-fill me-1"></i>Your account is assigned strictly to your own department.
                </div>
            <% } else { %>
                <p class="text-secondary mt-2">
                    <strong>Role Restriction:</strong> You do not possess the required user privileges 
                    to access this page or dashboard.
                </p>
            <% } %>

            <div class="d-flex justify-content-center gap-3 mt-4">
                <a href="<%= dashboardUrl %>" class="btn btn-primary px-4 py-2">
                    <i class="bi bi-house-door-fill me-1"></i>Return to My Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary px-3 py-2">
                    <i class="bi bi-box-arrow-right me-1"></i>Logout
                </a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
