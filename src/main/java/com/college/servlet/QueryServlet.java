package com.college.servlet;

import com.college.dao.QueryDAO;
import com.college.dao.UserDAO;
import com.college.model.QueryData;
import com.college.model.User;
import com.college.util.AccessControlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet for Student-Teacher Query / Doubt Inbox.
 * Routes:
 * - GET /queries (views student queries or teacher inbox based on role)
 * - GET /queries?action=ask (shows query form for student)
 * - POST /queries (student sends a query, or teacher submits an answer)
 */
@WebServlet("/queries")
public class QueryServlet extends HttpServlet {

    private final QueryDAO queryDAO = new QueryDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);
        Integer userId = AccessControlUtil.getSessionUserId(session);

        if (userId == null || departmentId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        // 1. Show query asking form (Student only)
        if ("ask".equalsIgnoreCase(action)) {
            if (!"STUDENT".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }

            // Load teachers of student's own department
            List<User> teachers = userDAO.getTeachersByDepartment(departmentId);
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("/query-form.jsp").forward(request, response);
            return;
        }

        // 2. Default: Inbox / Query list based on role
        if ("TEACHER".equalsIgnoreCase(role)) {
            // Teacher sees queries sent to them
            List<QueryData> queries = queryDAO.getQueriesForTeacher(userId, departmentId);
            request.setAttribute("queries", queries);
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            // Student sees all their own asked queries and answers
            List<QueryData> queries = queryDAO.getQueriesForStudent(userId);
            request.setAttribute("queries", queries);
        }

        request.getRequestDispatcher("/queries.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);
        Integer userId = AccessControlUtil.getSessionUserId(session);

        if (userId == null || departmentId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        // Action: Teacher replies to a query
        if ("reply".equalsIgnoreCase(action)) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }

            String queryIdStr = request.getParameter("queryId");
            String answer = request.getParameter("answer");

            if (queryIdStr == null || answer == null || answer.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/queries?error=empty_answer");
                return;
            }

            int queryId;
            try {
                queryId = Integer.parseInt(queryIdStr.trim());
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/queries?error=invalid_id");
                return;
            }

            // Security check: Query must exist and be assigned to this teacher
            QueryData existing = queryDAO.getQueryById(queryId);
            if (existing == null || existing.getTeacherId() != userId || existing.getDepartmentId() != departmentId) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
                return;
            }

            boolean ok = queryDAO.replyToQuery(queryId, userId, answer.trim());
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/queries?status=answered");
            } else {
                response.sendRedirect(request.getContextPath() + "/queries?error=failed");
            }
            return;
        }

        // Action: Student posts a new query
        if (!"STUDENT".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
            return;
        }

        String teacherIdStr = request.getParameter("teacherId");
        String question = request.getParameter("question");

        if (teacherIdStr == null || question == null || question.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please select a teacher and type your question!");
            request.setAttribute("teachers", userDAO.getTeachersByDepartment(departmentId));
            request.getRequestDispatcher("/query-form.jsp").forward(request, response);
            return;
        }

        int teacherId;
        try {
            teacherId = Integer.parseInt(teacherIdStr.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid teacher selection!");
            request.setAttribute("teachers", userDAO.getTeachersByDepartment(departmentId));
            request.getRequestDispatcher("/query-form.jsp").forward(request, response);
            return;
        }

        // Security check: Teacher must belong to student's department
        User teacher = userDAO.getUserById(teacherId);
        if (teacher == null || !"TEACHER".equalsIgnoreCase(teacher.getRole()) 
                || teacher.getDepartmentId() == null || !teacher.getDepartmentId().equals(departmentId)) {
            request.setAttribute("errorMessage", "You can only ask questions to teachers in your department!");
            request.setAttribute("teachers", userDAO.getTeachersByDepartment(departmentId));
            request.getRequestDispatcher("/query-form.jsp").forward(request, response);
            return;
        }

        QueryData q = new QueryData(userId, teacherId, departmentId, question.trim());
        boolean ok = queryDAO.sendQuery(q);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/queries?status=sent");
        } else {
            request.setAttribute("errorMessage", "Database error sending query. Please try again.");
            request.setAttribute("teachers", userDAO.getTeachersByDepartment(departmentId));
            request.getRequestDispatcher("/query-form.jsp").forward(request, response);
        }
    }
}
