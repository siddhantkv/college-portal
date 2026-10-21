package com.college.dao;

import com.college.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for User operations.
 * Handles Registration, BCrypt Login, and Admin approvals.
 */
public class UserDAO {

    /**
     * Checks whether an email already exists in the database.
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true if email exists
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Registers a new user.
     * Passwords must be pre-hashed using BCrypt before calling this method.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role, department_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                ps.setInt(5, user.getDepartmentId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setString(6, user.getStatus());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authenticates user using email and plain password.
     * Uses BCrypt.checkpw to verify hashed password safely.
     * @return User object if authentication succeeds, null otherwise.
     */
    public User login(String email, String plainPassword) {
        String sql = "SELECT u.id, u.name, u.email, u.password, u.role, u.department_id, u.status, u.created_at, d.name AS dept_name " +
                     "FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.id " +
                     "WHERE u.email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");

                    // Check plain password against stored BCrypt hash
                    if (BCrypt.checkpw(plainPassword, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setName(rs.getString("name"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(storedHash);
                        user.setRole(rs.getString("role"));

                        int deptId = rs.getInt("department_id");
                        if (!rs.wasNull()) {
                            user.setDepartmentId(deptId);
                        } else {
                            user.setDepartmentId(null);
                        }

                        user.setDepartmentName(rs.getString("dept_name"));
                        user.setStatus(rs.getString("status"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all teachers with 'PENDING' status for Admin approval dashboard.
     */
    public List<User> getPendingTeachers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.role, u.department_id, u.status, u.created_at, d.name AS dept_name " +
                     "FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.id " +
                     "WHERE u.role = 'TEACHER' AND u.status = 'PENDING' " +
                     "ORDER BY u.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setDepartmentId(rs.getInt("department_id"));
                user.setDepartmentName(rs.getString("dept_name"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(user);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching pending teachers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates user status (e.g., 'APPROVED' or 'REJECTED') by Admin.
     */
    public boolean updateStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating user status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves user by ID.
     */
    public User getUserById(int userId) {
        String sql = "SELECT u.id, u.name, u.email, u.role, u.department_id, u.status, u.created_at, d.name AS dept_name " +
                     "FROM users u " +
                     "LEFT JOIN departments d ON u.department_id = d.id " +
                     "WHERE u.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setDepartmentName(rs.getString("dept_name"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting user by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all approved teachers belonging to a specific department.
     * Used by students in Query / Doubt form to select which teacher to ask.
     */
    public List<User> getTeachersByDepartment(int departmentId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users " +
                     "WHERE role = 'TEACHER' AND status = 'APPROVED' AND department_id = ? " +
                     "ORDER BY name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User teacher = new User();
                    teacher.setId(rs.getInt("id"));
                    teacher.setName(rs.getString("name"));
                    teacher.setEmail(rs.getString("email"));
                    list.add(teacher);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching department teachers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}

