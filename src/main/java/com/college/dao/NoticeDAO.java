package com.college.dao;

import com.college.model.Notice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Notice operations.
 * Implements strict department filtering, teacher ownership validation,
 * and support for optional file/image attachments.
 */
public class NoticeDAO {

    /**
     * Inserts a new notice into the database (with optional attachment).
     */
    public boolean addNotice(Notice notice) {
        String sql = "INSERT INTO notices (title, content, teacher_id, department_id, attachment_name, original_attachment_name) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());
            ps.setInt(3, notice.getTeacherId());
            ps.setInt(4, notice.getDepartmentId());
            ps.setString(5, notice.getAttachmentName());
            ps.setString(6, notice.getOriginalAttachmentName());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing notice (title, content, and optional new attachment).
     */
    public boolean updateNotice(Notice notice) {
        String sql;
        boolean hasNewAttachment = notice.getAttachmentName() != null && !notice.getAttachmentName().isEmpty();
        
        if (hasNewAttachment) {
            sql = "UPDATE notices SET title = ?, content = ?, attachment_name = ?, original_attachment_name = ? WHERE id = ? AND teacher_id = ?";
        } else {
            sql = "UPDATE notices SET title = ?, content = ? WHERE id = ? AND teacher_id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, notice.getTitle());
            ps.setString(2, notice.getContent());

            if (hasNewAttachment) {
                ps.setString(3, notice.getAttachmentName());
                ps.setString(4, notice.getOriginalAttachmentName());
                ps.setInt(5, notice.getId());
                ps.setInt(6, notice.getTeacherId());
            } else {
                ps.setInt(3, notice.getId());
                ps.setInt(4, notice.getTeacherId());
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a notice.
     */
    public boolean deleteNotice(int noticeId, int teacherId) {
        String sql = "DELETE FROM notices WHERE id = ? AND teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, noticeId);
            ps.setInt(2, teacherId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting notice: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves a single notice by its ID.
     */
    public Notice getNoticeById(int noticeId) {
        String sql = "SELECT n.id, n.title, n.content, n.teacher_id, n.department_id, n.created_at, " +
                     "n.attachment_name, n.original_attachment_name, " +
                     "u.name AS teacher_name, d.name AS dept_name " +
                     "FROM notices n " +
                     "JOIN users u ON n.teacher_id = u.id " +
                     "JOIN departments d ON n.department_id = d.id " +
                     "WHERE n.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, noticeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToNotice(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting notice by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all notices for a specific department.
     */
    public List<Notice> getNoticesByDepartment(int departmentId) {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.id, n.title, n.content, n.teacher_id, n.department_id, n.created_at, " +
                     "n.attachment_name, n.original_attachment_name, " +
                     "u.name AS teacher_name, d.name AS dept_name " +
                     "FROM notices n " +
                     "JOIN users u ON n.teacher_id = u.id " +
                     "JOIN departments d ON n.department_id = d.id " +
                     "WHERE n.department_id = ? " +
                     "ORDER BY n.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToNotice(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching department notices: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Helper to map a ResultSet row to a Notice object.
     */
    private Notice mapRowToNotice(ResultSet rs) throws SQLException {
        Notice notice = new Notice();
        notice.setId(rs.getInt("id"));
        notice.setTitle(rs.getString("title"));
        notice.setContent(rs.getString("content"));
        notice.setTeacherId(rs.getInt("teacher_id"));
        notice.setTeacherName(rs.getString("teacher_name"));
        notice.setDepartmentId(rs.getInt("department_id"));
        notice.setDepartmentName(rs.getString("dept_name"));
        notice.setAttachmentName(rs.getString("attachment_name"));
        notice.setOriginalAttachmentName(rs.getString("original_attachment_name"));
        notice.setCreatedAt(rs.getTimestamp("created_at"));
        return notice;
    }
}
