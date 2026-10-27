<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.college.model.Notice" %>
<%
    String role = (String) session.getAttribute("role");
    if (!"TEACHER".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
        return;
    }

    Notice notice = (Notice) request.getAttribute("notice");
    boolean isEdit = (notice != null && notice.getId() > 0);

    String error = (String) request.getAttribute("errorMessage");
    String deptName = (String) session.getAttribute("departmentName");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Edit Notice" : "Post Notice" %> - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <style>
        .form-card {
            max-width: 650px;
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
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#noticeFormNav" aria-controls="noticeFormNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse mt-2 mt-lg-0" id="noticeFormNav">
                <div class="ms-auto d-flex flex-column flex-lg-row align-items-lg-center gap-2">
                    <a href="${pageContext.request.contextPath}/notices" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-arrow-left me-1"></i>Back to Notices
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                        <i class="bi bi-box-arrow-right me-1"></i>Logout
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <!-- Form Container -->
    <div class="container">
        <div class="card form-card shadow p-4 bg-white">
            <div class="mb-4 text-center">
                <h3 class="fw-bold text-primary mb-1">
                    <i class="bi <%= isEdit ? "bi-pencil-square" : "bi-megaphone-fill" %> me-2"></i>
                    <%= isEdit ? "Edit Notice" : "Post New Notice" %>
                </h3>
                <p class="text-muted small">Department: <strong><%= deptName %></strong></p>
            </div>

            <!-- Error Alert -->
            <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Form -->
            <form action="${pageContext.request.contextPath}/notices" method="POST" enctype="multipart/form-data">
                
                <!-- Hidden Notice ID (0 for new, > 0 for edit) -->
                <input type="hidden" name="id" value="<%= isEdit ? notice.getId() : 0 %>">

                <!-- Notice Title -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Notice Title</label>
                    <input type="text" name="title" class="form-control" 
                           placeholder="e.g. Mid-term Exam Schedule Announced" 
                           maxlength="200"
                           value="<%= isEdit ? notice.getTitle() : "" %>" required>
                    <div class="form-text text-muted">A clear, brief heading (max 200 characters).</div>
                </div>

                <!-- Notice Content -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Notice Message / Details</label>
                    <textarea name="content" class="form-control" rows="6" 
                              placeholder="Write the full announcement, dates, requirements, or guidelines..." 
                              required><%= isEdit ? notice.getContent() : "" %></textarea>
                </div>

                <!-- Optional Circular / Image Attachment -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">
                        <i class="bi bi-paperclip me-1 text-primary"></i>Attach Document or Notice Image (Optional)
                    </label>
                    <input type="file" name="attachment" class="form-control"
                           accept=".pdf,.doc,.docx,.ppt,.pptx,.jpg,.jpeg,.png,.webp,.gif">
                    <div class="form-text text-muted mt-1">
                        Upload official circular photos (JPG, PNG, WEBP), scanned timetables, or PDF circulars (Max 10 MB).
                    </div>
                    <% if (isEdit && notice.hasAttachment()) { %>
                        <div class="mt-2 small text-success">
                            <i class="bi bi-check-circle me-1"></i>Current attachment: 
                            <strong><%= notice.getOriginalAttachmentName() %></strong> (Leave empty to keep existing).
                        </div>
                    <% } %>
                </div>

                <!-- Submit / Cancel Buttons -->
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${pageContext.request.contextPath}/notices" class="btn btn-outline-secondary px-4">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary px-4 fw-semibold shadow-sm">
                        <i class="bi <%= isEdit ? "bi-check-circle-fill" : "bi-send-fill" %> me-1"></i>
                        <%= isEdit ? "Update Notice" : "Publish Notice" %>
                    </button>
                </div>

            </form>
        </div>
    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
