<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.college.model.QueryData" %>
<%@ page import="java.util.List" %>
<%
    String role = (String) session.getAttribute("role");
    String userName = (String) session.getAttribute("userName");
    String deptName = (String) session.getAttribute("departmentName");
    List<QueryData> queries = (List<QueryData>) request.getAttribute("queries");
    String status = request.getParameter("status");
    String error = request.getParameter("error");
    boolean isTeacher = "TEACHER".equalsIgnoreCase(role);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isTeacher ? "Teacher Query Inbox" : "My Queries" %> - College Portal</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
        .query-card { border-radius: 10px; border-left: 5px solid #6c757d; }
        .query-card.pending { border-left-color: #ffc107; }
        .query-card.answered { border-left-color: #198754; }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark <%= isTeacher ? "bg-primary" : "bg-success" %> shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/<%= isTeacher ? "teacher-dashboard.jsp" : "student-dashboard.jsp" %>">
                <i class="bi bi-mortarboard-fill me-2"></i>College Portal
            </a>
            <div class="d-flex align-items-center text-white">
                <span class="me-3 small">
                    <i class="bi bi-person-circle me-1"></i><%= userName %> (<%= deptName %>)
                </span>
                <a href="${pageContext.request.contextPath}/<%= isTeacher ? "teacher-dashboard.jsp" : "student-dashboard.jsp" %>" class="btn btn-outline-light btn-sm me-2">
                    <i class="bi bi-house-door"></i> Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container py-4">

        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
            <div>
                <h3 class="fw-bold mb-0">
                    <i class="bi <%= isTeacher ? "bi-inbox-fill text-primary" : "bi-chat-dots-fill text-success" %> me-2"></i>
                    <%= isTeacher ? "Student Doubt Inbox" : "My Inquiries & Answers" %>
                </h3>
                <p class="text-muted small mb-0">
                    Department: <strong><%= deptName %></strong> &bull; <%= isTeacher ? "View doubts asked by students and reply." : "Ask doubts to teachers and read replies." %>
                </p>
            </div>
            <% if (!isTeacher) { %>
                <a href="${pageContext.request.contextPath}/queries?action=ask" class="btn btn-success">
                    <i class="bi bi-patch-question-fill me-1"></i>Ask New Doubt
                </a>
            <% } %>
        </div>

        <!-- Success/Error Alerts -->
        <% if ("sent".equalsIgnoreCase(status)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>Your question has been sent to the teacher successfully!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if ("answered".equalsIgnoreCase(status)) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill me-2"></i>Reply submitted successfully! The student can now see your answer.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } else if (error != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>Action could not be completed. Please check your inputs.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        <% } %>

        <!-- Queries List -->
        <% if (queries != null && !queries.isEmpty()) { %>
            <div class="d-flex flex-column gap-3">
                <% for (QueryData q : queries) { %>
                    <div class="card query-card shadow-sm p-4 bg-white <%= q.isPending() ? "pending" : "answered" %>">
                        
                        <!-- Header info -->
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                            <div>
                                <% if (isTeacher) { %>
                                    <span class="fw-bold text-dark">
                                        <i class="bi bi-person-fill text-primary me-1"></i>Student: <%= q.getStudentName() %>
                                    </span>
                                <% } else { %>
                                    <span class="fw-bold text-dark">
                                        <i class="bi bi-person-badge-fill text-success me-1"></i>To: Prof. <%= q.getTeacherName() %>
                                    </span>
                                <% } %>
                                <span class="badge <%= q.isPending() ? "bg-warning text-dark" : "bg-success" %> ms-2">
                                    <i class="bi <%= q.isPending() ? "bi-hourglass-split" : "bi-check2-all" %> me-1"></i>
                                    <%= q.getStatus() %>
                                </span>
                            </div>
                            <span class="small text-muted">
                                <i class="bi bi-clock me-1"></i><%= q.getCreatedAt() != null ? q.getCreatedAt().toString().substring(0, 16) : "" %>
                            </span>
                        </div>

                        <!-- Question Box -->
                        <div class="p-3 bg-light rounded mb-3 border">
                            <h6 class="fw-semibold text-secondary mb-1"><i class="bi bi-question-circle me-1"></i>Question:</h6>
                            <p class="mb-0 text-dark" style="white-space: pre-wrap;"><%= q.getQuestion() %></p>
                        </div>

                        <!-- Answer Display (if answered) -->
                        <% if (q.isAnswered()) { %>
                            <div class="p-3 bg-success-subtle rounded border border-success-subtle mb-2">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <h6 class="fw-bold text-success mb-0">
                                        <i class="bi bi-reply-fill me-1"></i>Prof. <%= q.getTeacherName() %>'s Reply:
                                    </h6>
                                    <small class="text-muted">
                                        Answered on: <%= q.getAnsweredAt() != null ? q.getAnsweredAt().toString().substring(0, 16) : "" %>
                                    </small>
                                </div>
                                <p class="mb-0 text-dark" style="white-space: pre-wrap;"><%= q.getAnswer() %></p>
                            </div>
                        <% } else if (isTeacher) { %>
                            <!-- Teacher Reply Form (if pending) -->
                            <div class="mt-2 pt-2 border-top">
                                <form action="${pageContext.request.contextPath}/queries" method="POST">
                                    <input type="hidden" name="action" value="reply">
                                    <input type="hidden" name="queryId" value="<%= q.getId() %>">
                                    <label class="form-label fw-semibold small text-primary">Write Answer / Clarification:</label>
                                    <div class="input-group">
                                        <textarea name="answer" class="form-control" rows="2" placeholder="Type solution or reply to the student..." required></textarea>
                                        <button class="btn btn-primary" type="submit">
                                            <i class="bi bi-send me-1"></i>Send Reply
                                        </button>
                                    </div>
                                </form>
                            </div>
                        <% } else { %>
                            <!-- Student Waiting Notice -->
                            <div class="small text-muted fst-italic">
                                <i class="bi bi-info-circle me-1 text-warning"></i>Waiting for Prof. <%= q.getTeacherName() %> to respond...
                            </div>
                        <% } %>

                    </div>
                <% } %>
            </div>
        <% } else { %>
            <div class="card shadow-sm border-0 text-center py-5 bg-white">
                <i class="bi bi-chat-square-dots text-muted display-4"></i>
                <h5 class="fw-semibold mt-3 text-secondary">
                    <%= isTeacher ? "Your query inbox is clear!" : "You haven't asked any doubts yet." %>
                </h5>
                <p class="text-muted small">
                    <%= isTeacher ? "When students from your department ask you questions, they will show up here." : "Have a question about assignments or syllabus? Reach out to your subject teachers anytime!" %>
                </p>
                <% if (!isTeacher) { %>
                    <div class="mt-2">
                        <a href="${pageContext.request.contextPath}/queries?action=ask" class="btn btn-success btn-sm">
                            <i class="bi bi-plus-lg me-1"></i>Ask Your First Doubt
                        </a>
                    </div>
                <% } %>
            </div>
        <% } %>

    </div>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
