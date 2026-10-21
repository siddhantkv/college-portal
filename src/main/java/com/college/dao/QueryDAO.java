package com.college.dao;

import com.college.model.QueryData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling Student Queries and Teacher Replies.
 * Implements inbox-style workflow and strict department isolation.
 */
public class QueryDAO {

    /**
     * Inserts a new question asked by a student.
     * Initial status is always 'PENDING'.
     */
    public boolean sendQuery(QueryData query) {
        String sql = "INSERT INTO queries (student_id, teacher_id, department_id, question, status) VALUES (?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, query.getStudentId());
            ps.setInt(2, query.getTeacherId());
            ps.setInt(3, query.getDepartmentId());
            ps.setString(4, query.getQuestion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error sending query: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Teacher inbox: Retrieves all queries directed to a specific teacher.
     */
    public List<QueryData> getQueriesForTeacher(int teacherId, int departmentId) {
        List<QueryData> list = new ArrayList<>();
        String sql = "SELECT q.id, q.student_id, q.teacher_id, q.department_id, q.question, q.answer, " +
                     "q.status, q.created_at, q.answered_at, " +
                     "stu.name AS student_name, tea.name AS teacher_name, d.name AS dept_name " +
                     "FROM queries q " +
                     "JOIN users stu ON q.student_id = stu.id " +
                     "JOIN users tea ON q.teacher_id = tea.id " +
                     "JOIN departments d ON q.department_id = d.id " +
                     "WHERE q.teacher_id = ? AND q.department_id = ? " +
                     "ORDER BY CASE WHEN q.status = 'PENDING' THEN 1 ELSE 2 END, q.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);
            ps.setInt(2, departmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToQuery(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching teacher queries: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Student view: Retrieves all queries asked by a specific student.
     */
    public List<QueryData> getQueriesForStudent(int studentId) {
        List<QueryData> list = new ArrayList<>();
        String sql = "SELECT q.id, q.student_id, q.teacher_id, q.department_id, q.question, q.answer, " +
                     "q.status, q.created_at, q.answered_at, " +
                     "stu.name AS student_name, tea.name AS teacher_name, d.name AS dept_name " +
                     "FROM queries q " +
                     "JOIN users stu ON q.student_id = stu.id " +
                     "JOIN users tea ON q.teacher_id = tea.id " +
                     "JOIN departments d ON q.department_id = d.id " +
                     "WHERE q.student_id = ? " +
                     "ORDER BY q.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToQuery(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching student queries: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Fetches a single query by its primary ID.
     */
    public QueryData getQueryById(int queryId) {
        String sql = "SELECT q.id, q.student_id, q.teacher_id, q.department_id, q.question, q.answer, " +
                     "q.status, q.created_at, q.answered_at, " +
                     "stu.name AS student_name, tea.name AS teacher_name, d.name AS dept_name " +
                     "FROM queries q " +
                     "JOIN users stu ON q.student_id = stu.id " +
                     "JOIN users tea ON q.teacher_id = tea.id " +
                     "JOIN departments d ON q.department_id = d.id " +
                     "WHERE q.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, queryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuery(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching query by id: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Teacher answers a query.
     * Updates answer text, sets status to 'ANSWERED', and records current timestamp.
     * Security check: WHERE id = ? AND teacher_id = ? ensures only the assigned teacher can reply.
     */
    public boolean replyToQuery(int queryId, int teacherId, String answer) {
        String sql = "UPDATE queries SET answer = ?, status = 'ANSWERED', answered_at = CURRENT_TIMESTAMP " +
                     "WHERE id = ? AND teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, answer);
            ps.setInt(2, queryId);
            ps.setInt(3, teacherId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error replying to query: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private QueryData mapRowToQuery(ResultSet rs) throws SQLException {
        QueryData q = new QueryData();
        q.setId(rs.getInt("id"));
        q.setStudentId(rs.getInt("student_id"));
        q.setTeacherId(rs.getInt("teacher_id"));
        q.setDepartmentId(rs.getInt("department_id"));
        q.setQuestion(rs.getString("question"));
        q.setAnswer(rs.getString("answer"));
        q.setStatus(rs.getString("status"));
        q.setCreatedAt(rs.getTimestamp("created_at"));
        q.setAnsweredAt(rs.getTimestamp("answered_at"));
        q.setStudentName(rs.getString("student_name"));
        q.setTeacherName(rs.getString("teacher_name"));
        q.setDepartmentName(rs.getString("dept_name"));
        return q;
    }
}
