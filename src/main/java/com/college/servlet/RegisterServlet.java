package com.college.servlet;

import com.college.dao.DepartmentDAO;
import com.college.dao.UserDAO;
import com.college.model.Department;
import com.college.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet for User Registration.
 * Handles both GET (showing registration form with departments) 
 * and POST (validating inputs, hashing password with BCrypt, and saving to DB).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Fetch all departments to populate the dropdown in the form
        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);

        // Forward to register.jsp
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Read Form Inputs
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String departmentIdStr = request.getParameter("departmentId");

        // Keep values in request so form doesn't reset on error
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("role", role);
        request.setAttribute("selectedDeptId", departmentIdStr);

        // Re-load departments in case of validation error
        request.setAttribute("departments", departmentDAO.getAllDepartments());

        // 2. Server-side Validation
        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            role == null || role.trim().isEmpty()) {
            
            request.setAttribute("errorMessage", "All fields are required!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Validate Role (only TEACHER or STUDENT allowed to register)
        if (!"TEACHER".equalsIgnoreCase(role) && !"STUDENT".equalsIgnoreCase(role)) {
            request.setAttribute("errorMessage", "Invalid role selected!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Validate Department
        int departmentId = 0;
        try {
            departmentId = Integer.parseInt(departmentIdStr);
            if (departmentId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Please select a valid Department!");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Check if Email is already registered
        User existingUser = userDAO.getUserByEmail(email);
        boolean isReapplying = false;

        if (existingUser != null) {
            // If the account was previously REJECTED, allow re-applying by updating the record
            if ("REJECTED".equalsIgnoreCase(existingUser.getStatus())) {
                isReapplying = true;
            } else {
                request.setAttribute("errorMessage", "This email is already registered. Please login instead.");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
        }

        // 3. Password Hashing using BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // 4. Role Status Logic:
        // - Teacher accounts remain 'PENDING' until Admin approves them.
        // - Student accounts are immediately 'APPROVED'.
        String status = "STUDENT".equalsIgnoreCase(role) ? "APPROVED" : "PENDING";

        User userRecord = new User(name.trim(), email.trim().toLowerCase(), hashedPassword, role.toUpperCase(), departmentId, status);

        // 5. Save to Database (Insert new or update existing rejected record)
        boolean isSuccess;
        if (isReapplying) {
            isSuccess = userDAO.reapplyUser(userRecord);
        } else {
            isSuccess = userDAO.registerUser(userRecord);
        }

        if (isSuccess) {
            if ("TEACHER".equalsIgnoreCase(role)) {
                if (isReapplying) {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?registered=reapplied");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?registered=pending");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?registered=success");
            }
        } else {
            request.setAttribute("errorMessage", "Registration failed due to a server error. Please try again.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
