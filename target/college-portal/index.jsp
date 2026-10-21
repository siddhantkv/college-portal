<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>College Communication Portal</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Simple Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">🎓 College Portal</a>
            <div class="ms-auto">
                <a href="login.jsp" class="btn btn-outline-light me-2">Login</a>
                <a href="register.jsp" class="btn btn-warning">Register</a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="container my-5 text-center">
        <div class="card shadow-sm p-5 border-0">
            <h1 class="display-5 fw-bold text-primary mb-3">Welcome to College Communication Portal</h1>
            <p class="lead text-secondary">
                Department-wise safe and organized communication platform for Students, Teachers and Admin.
            </p>
            <hr class="my-4">
            <div class="alert alert-success d-inline-block px-4 py-2">
                ✅ <strong>Server & Setup Ready!</strong> Tomcat 10 + JSP is working fine.
            </div>
        </div>
    </div>

</body>
</html>
