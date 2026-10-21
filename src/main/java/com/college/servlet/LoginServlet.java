package com.college.servlet;

import com.college.dao.UserDAO;
import com.college.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller servlet for User Login.
 * Verifies credentials, verifies approval status, sets up the HTTP session,
 * and redirects user to their appropriate role-specific dashboard.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to login page
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Keep email in input box if login fails
        request.setAttribute("email", email);

        // 1. Basic validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and Password are required!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // 2. Authenticate user via UserDAO
        User user = userDAO.login(email.trim(), password);

        if (user == null) {
            request.setAttribute("errorMessage", "Invalid email or password!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // 3. Status check: Teachers must be approved by Admin
        if ("PENDING".equalsIgnoreCase(user.getStatus())) {
            request.setAttribute("errorMessage", "Your account is pending Admin approval. Please contact administration.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if ("REJECTED".equalsIgnoreCase(user.getStatus())) {
            request.setAttribute("errorMessage", "Your account registration has been rejected by the administrator.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // 4. Create HTTP Session and store crucial identity & department data
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("userName", user.getName());
        session.setAttribute("userEmail", user.getEmail());
        session.setAttribute("role", user.getRole());
        session.setAttribute("departmentId", user.getDepartmentId());
        session.setAttribute("departmentName", user.getDepartmentName());

        // 5. Role-based Redirection
        String role = user.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else if ("TEACHER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/teacher-dashboard.jsp");
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/student-dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
