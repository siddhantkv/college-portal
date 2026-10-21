<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String error = (String) request.getAttribute("errorMessage");
    String email = (String) request.getAttribute("email");
    String regParam = request.getParameter("registered");
    String loggedOut = request.getParameter("loggedOut");
    String authError = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - College Communication Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .card-login {
            max-width: 440px;
            margin: 70px auto;
            border-radius: 12px;
            border: none;
        }
    </style>
</head>
<body>

    <!-- Simple Top Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/index.jsp">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <div class="ms-auto">
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-warning btn-sm fw-semibold">Register</a>
            </div>
        </div>
    </nav>

    <!-- Login Container -->
    <div class="container">
        <div class="card card-login shadow p-4 bg-white">
            <div class="text-center mb-4">
                <div class="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-2" style="width: 55px; height: 55px;">
                    <i class="bi bi-person-fill fs-3"></i>
                </div>
                <h3 class="fw-bold text-dark">Portal Login</h3>
                <p class="text-muted small">Enter credentials to access your dashboard</p>
            </div>

            <!-- Success / Info Alerts -->
            <% if ("success".equalsIgnoreCase(regParam)) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill me-2"></i>Registration successful! Please login.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } else if ("pending".equalsIgnoreCase(regParam)) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-clock-history me-2"></i>Registration submitted! Your <strong>Teacher account is pending Admin approval</strong> before you can login.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <% if ("true".equalsIgnoreCase(loggedOut)) { %>
                <div class="alert alert-info alert-dismissible fade show" role="alert">
                    <i class="bi bi-info-circle-fill me-2"></i>You have been logged out safely.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <% if ("unauthorized".equalsIgnoreCase(authError)) { %>
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <i class="bi bi-shield-lock-fill me-2"></i>Please login to access protected pages.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Error Alert -->
            <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="POST">
                
                <!-- Email -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email" name="email" class="form-control" placeholder="user@college.com" 
                               value="<%= email != null ? email : "" %>" required>
                    </div>
                </div>

                <!-- Password -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Password</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-lock"></i></span>
                        <input type="password" name="password" class="form-control" placeholder="Enter password" required>
                    </div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                    <i class="bi bi-box-arrow-in-right me-1"></i>Login
                </button>
            </form>

            <!-- Quick Demo Credentials Box for Evaluation / Viva -->
            <div class="card bg-light border-0 mt-4 p-2 text-center">
                <small class="text-muted fw-semibold">Default Admin Credentials:</small>
                <code class="small text-primary">admin@college.com / admin123</code>
            </div>

            <div class="text-center mt-3">
                <span class="text-muted">Don't have an account?</span>
                <a href="${pageContext.request.contextPath}/register.jsp" class="text-decoration-none fw-semibold">Register here</a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
