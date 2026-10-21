<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.college.model.Department" %>
<%@ page import="com.college.dao.DepartmentDAO" %>
<%
    // Ensure departments list is available even if user directly visits register.jsp
    List<Department> departments = (List<Department>) request.getAttribute("departments");
    if (departments == null) {
        DepartmentDAO deptDAO = new DepartmentDAO();
        departments = deptDAO.getAllDepartments();
    }
    String error = (String) request.getAttribute("errorMessage");
    String name = (String) request.getAttribute("name");
    String email = (String) request.getAttribute("email");
    String role = (String) request.getAttribute("role");
    String selectedDeptId = (String) request.getAttribute("selectedDeptId");
    String dbError = DepartmentDAO.lastError;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - College Communication Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f4f6f9;
        }
        .card-register {
            max-width: 520px;
            margin: 50px auto;
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
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-outline-light btn-sm">Login</a>
            </div>
        </div>
    </nav>

    <!-- Registration Container -->
    <div class="container">
        <div class="card card-register shadow p-4 bg-white">
            <div class="text-center mb-4">
                <h3 class="fw-bold text-primary">Create Account</h3>
                <p class="text-muted small">Join your college department portal</p>
            </div>

            <!-- Database Diagnostic Notice if Departments not loaded -->
            <% if (departments == null || departments.isEmpty()) { %>
                <div class="alert alert-warning text-start p-3 mb-3 small" role="alert">
                    <h6 class="alert-heading fw-bold text-dark mb-1">
                        <i class="bi bi-database-exclamation text-danger me-1"></i>Database Connection Notice
                    </h6>
                    <p class="mb-1 text-secondary">
                        Departments load nahi hue kyunki MySQL database connect nahi ho pa raha:
                    </p>
                    <% if (dbError != null) { %>
                        <div class="bg-dark text-warning p-2 rounded my-2 font-monospace" style="font-size: 0.8rem; word-break: break-all;">
                            <%= dbError %>
                        </div>
                    <% } %>
                    <div class="mt-2 text-dark">
                        <strong>Iska solution:</strong>
                        <ul class="ps-3 mb-0 mt-1">
                            <li>Agar <strong>Access denied</strong> hai: <code>DBConnection.java</code> (line 14) par apna MySQL password set karein.</li>
                            <li>Agar <strong>Unknown database</strong> hai: MySQL Workbench mein <code>database/schema.sql</code> run karein.</li>
                        </ul>
                    </div>
                </div>
            <% } %>

            <!-- Error Alert -->
            <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            <% } %>

            <!-- Form -->
            <form action="${pageContext.request.contextPath}/register" method="POST" id="regForm">
                
                <!-- Full Name -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Full Name</label>
                    <input type="text" name="name" class="form-control" placeholder="e.g. Rahul Sharma" 
                           value="<%= name != null ? name : "" %>" required>
                </div>

                <!-- Email -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">College / Personal Email</label>
                    <input type="email" name="email" class="form-control" placeholder="name@example.com" 
                           value="<%= email != null ? email : "" %>" required>
                </div>

                <!-- Password -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Password</label>
                    <input type="password" name="password" class="form-control" placeholder="Minimum 6 characters" minlength="6" required>
                </div>

                <!-- Role Selection -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Register As</label>
                    <select name="role" id="roleSelect" class="form-select" required onchange="handleRoleChange()">
                        <option value="">-- Select Role --</option>
                        <option value="STUDENT" <%= "STUDENT".equalsIgnoreCase(role) ? "selected" : "" %>>Student</option>
                        <option value="TEACHER" <%= "TEACHER".equalsIgnoreCase(role) ? "selected" : "" %>>Teacher (Requires Admin Approval)</option>
                    </select>
                    <div id="teacherNote" class="form-text text-warning d-none">
                        <i class="bi bi-info-circle me-1"></i>Teacher accounts remain pending until Admin approves.
                    </div>
                </div>

                <!-- Department Dropdown -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Department</label>
                    <select name="departmentId" class="form-select" required>
                        <option value="">-- Select Your Department --</option>
                        <% if (departments != null) {
                            for (Department d : departments) {
                                boolean isSel = selectedDeptId != null && selectedDeptId.equals(String.valueOf(d.getId()));
                        %>
                            <option value="<%= d.getId() %>" <%= isSel ? "selected" : "" %>>
                                <%= d.getName() %> (<%= d.getCode() %>)
                            </option>
                        <%  }
                        } %>
                    </select>
                    <div class="form-text text-muted">You will only see content belonging to this department.</div>
                </div>

                <!-- Submit Button -->
                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                    <i class="bi bi-person-plus-fill me-1"></i>Register
                </button>
            </form>

            <div class="text-center mt-4">
                <span class="text-muted">Already have an account?</span>
                <a href="${pageContext.request.contextPath}/login.jsp" class="text-decoration-none fw-semibold">Log in here</a>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function handleRoleChange() {
            const role = document.getElementById("roleSelect").value;
            const note = document.getElementById("teacherNote");
            if (role === "TEACHER") {
                note.classList.remove("d-none");
            } else {
                note.classList.add("d-none");
            }
        }
        // Run on load to retain state
        handleRoleChange();
    </script>
</body>
</html>
