package com.college.dao;

import com.college.model.FileData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for File operations.
 * Handles metadata persistence, department filtering, and ownership validation.
 */
public class FileDAO {

    /**
     * Saves uploaded file metadata in the database.
     */
    public boolean saveFile(FileData fileData) {
        String sql = "INSERT INTO files (title, description, file_name, original_name, file_size, file_type, teacher_id, department_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fileData.getTitle());
            ps.setString(2, fileData.getDescription());
            ps.setString(3, fileData.getFileName());
            ps.setString(4, fileData.getOriginalName());
            ps.setLong(5, fileData.getFileSize());
            ps.setString(6, fileData.getFileType());
            ps.setInt(7, fileData.getTeacherId());
            ps.setInt(8, fileData.getDepartmentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error saving file metadata: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves file metadata by ID.
     */
    public FileData getFileById(int id) {
        String sql = "SELECT f.id, f.title, f.description, f.file_name, f.original_name, f.file_size, f.file_type, " +
                     "f.teacher_id, f.department_id, f.uploaded_at, u.name AS teacher_name, d.name AS dept_name " +
                     "FROM files f " +
                     "JOIN users u ON f.teacher_id = u.id " +
                     "JOIN departments d ON f.department_id = d.id " +
                     "WHERE f.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFileData(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting file by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all files for a department in reverse chronological order.
     * Core security rule: WHERE f.department_id = ?
     */
    public List<FileData> getFilesByDepartment(int departmentId) {
        List<FileData> list = new ArrayList<>();
        String sql = "SELECT f.id, f.title, f.description, f.file_name, f.original_name, f.file_size, f.file_type, " +
                     "f.teacher_id, f.department_id, f.uploaded_at, u.name AS teacher_name, d.name AS dept_name " +
                     "FROM files f " +
                     "JOIN users u ON f.teacher_id = u.id " +
                     "JOIN departments d ON f.department_id = d.id " +
                     "WHERE f.department_id = ? " +
                     "ORDER BY f.uploaded_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFileData(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching department files: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Deletes file metadata from database.
     * Security check: WHERE id = ? AND teacher_id = ? ensures only the uploading teacher can delete.
     */
    public boolean deleteFile(int fileId, int teacherId) {
        String sql = "DELETE FROM files WHERE id = ? AND teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, fileId);
            ps.setInt(2, teacherId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting file metadata: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private FileData mapRowToFileData(ResultSet rs) throws SQLException {
        FileData file = new FileData();
        file.setId(rs.getInt("id"));
        file.setTitle(rs.getString("title"));
        file.setDescription(rs.getString("description"));
        file.setFileName(rs.getString("file_name"));
        file.setOriginalName(rs.getString("original_name"));
        file.setFileSize(rs.getLong("file_size"));
        file.setFileType(rs.getString("file_type"));
        file.setTeacherId(rs.getInt("teacher_id"));
        file.setTeacherName(rs.getString("teacher_name"));
        file.setDepartmentId(rs.getInt("department_id"));
        file.setDepartmentName(rs.getString("dept_name"));
        file.setUploadedAt(rs.getTimestamp("uploaded_at"));
        return file;
    }
}
