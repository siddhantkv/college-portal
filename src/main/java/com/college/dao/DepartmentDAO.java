package com.college.dao;

import com.college.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for handling Department database operations.
 */
public class DepartmentDAO {

    public static String lastError = null;

    /**
     * Retrieves all departments from the database.
     * Useful for populating department dropdown in registration form.
     */
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        lastError = null;
        String sql = "SELECT id, name, code FROM departments ORDER BY name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("id"));
                dept.setName(rs.getString("name"));
                dept.setCode(rs.getString("code"));
                list.add(dept);
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.err.println("❌ Error fetching departments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves a department by its ID.
     */
    public Department getDepartmentById(int id) {
        String sql = "SELECT id, name, code FROM departments WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Department dept = new Department();
                    dept.setId(rs.getInt("id"));
                    dept.setName(rs.getString("name"));
                    dept.setCode(rs.getString("code"));
                    return dept;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching department by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
