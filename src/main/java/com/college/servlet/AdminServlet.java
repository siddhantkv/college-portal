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
import java.util.List;

/**
 * Controller servlet for Admin operations.
 * Allows Admin to view and approve/reject pending teacher accounts.
 */
@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Security check: Only Admin can access
        if (session == null || !"ADMIN".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");
        String idStr = request.getParameter("id");

        // Handle Approve / Reject actions
        if (action != null && idStr != null) {
            try {
                int teacherId = Integer.parseInt(idStr);
                if ("approve".equalsIgnoreCase(action)) {
                    userDAO.updateStatus(teacherId, "APPROVED");
                    response.sendRedirect(request.getContextPath() + "/admin?status=approved");
                    return;
                } else if ("reject".equalsIgnoreCase(action)) {
                    userDAO.updateStatus(teacherId, "REJECTED");
                    response.sendRedirect(request.getContextPath() + "/admin?status=rejected");
                    return;
                } else if ("delete".equalsIgnoreCase(action)) {
                    userDAO.deleteUser(teacherId);
                    response.sendRedirect(request.getContextPath() + "/admin?status=deleted");
                    return;
                }
            } catch (NumberFormatException e) {
                // Ignore invalid ID
            }
        }

        // Fetch all pending teacher registrations
        List<User> pendingTeachers = userDAO.getPendingTeachers();
        request.setAttribute("pendingTeachers", pendingTeachers);

        // Fetch all active/approved teachers for removal management
        List<User> activeTeachers = userDAO.getActiveTeachers();
        request.setAttribute("activeTeachers", activeTeachers);

        // Forward to admin dashboard JSP
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }
}
