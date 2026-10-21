<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.college.model.User" %>
<%@ page import="java.util.List" %>
<%
    String role = (String) session.getAttribute("role");
    String userName = (String) session.getAttribute("userName");
    String deptName = (String) session.getAttribute("departmentName");
    String error = (String) request.getAttribute("errorMessage");
    List<User> teachers = (List<User>) request.getAttribute("teachers");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ask a Teacher - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .form-card { max-width: 680px; margin: 40px auto; border-radius: 12px; border: none; }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-success shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/student-dashboard.jsp">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <div class="d-flex align-items-center text-white">
                <span class="me-3 small">
                    <i class="bi bi-person-circle me-1"></i><%= userName %> (<%= deptName %>)
                </span>
                <a href="${pageContext.request.contextPath}/queries" class="btn btn-outline-light btn-sm me-2">
                    <i class="bi bi-chat-dots-fill me-1"></i>My Queries
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Content -->
    <div class="container">
        <div class="card form-card shadow p-4 bg-white">
            <div class="mb-4 text-center">
                <h3 class="fw-bold text-success mb-1">
                    <i class="bi bi-question-circle-fill me-2"></i>Ask a Teacher
                </h3>
                <p class="text-muted small">Department: <strong><%= deptName %></strong> &bull; Inbox Doubt Clearing</p>
            </div>

            <!-- Error Alert -->
            <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Form -->
            <form action="${pageContext.request.contextPath}/queries" method="POST">
                
                <!-- Teacher Selection -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Select Teacher</label>
                    <select name="teacherId" class="form-select" required>
                        <option value="">-- Choose a teacher from your department --</option>
                        <% if (teachers != null && !teachers.isEmpty()) {
                            for (User t : teachers) { %>
                                <option value="<%= t.getId() %>">Prof. <%= t.getName() %> (<%= t.getEmail() %>)</option>
                        <%  } 
                        } else { %>
                            <option value="" disabled>No approved teachers found in your department</option>
                        <% } %>
                    </select>
                    <div class="form-text text-muted">Only teachers registered and approved in your department are listed.</div>
                </div>

                <!-- Query / Question Content -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Your Question / Doubt</label>
                    <textarea name="question" class="form-control" rows="6" 
                              placeholder="Type your question or clarification in detail (topics, assignment questions, syllabus doubts, etc.)..." 
                              required></textarea>
                </div>

                <!-- Buttons -->
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/queries" class="btn btn-outline-secondary px-4">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-success px-4 fw-semibold shadow-sm">
                        <i class="bi bi-send-fill me-1"></i>Send Query
                    </button>
                </div>

            </form>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
