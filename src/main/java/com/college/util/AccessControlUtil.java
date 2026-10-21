package com.college.util;

import jakarta.servlet.http.HttpSession;

/**
 * Access Control Utility to enforce strict department-level isolation.
 * Ensures:
 * 1. An MCA student/teacher can ONLY see MCA notices, files, and queries.
 * 2. Cross-department data access via manipulated URL parameters (e.g. ?id=99) is blocked.
 * 3. Teachers can only edit/delete their own posted content.
 */
public class AccessControlUtil {

    /**
     * Extracts the user's department ID from the active session.
     * @param session Current HTTP session
     * @return Department ID or null if not set
     */
    public static Integer getSessionDepartmentId(HttpSession session) {
        if (session == null) return null;
        Object deptIdObj = session.getAttribute("departmentId");
        if (deptIdObj instanceof Integer) {
            return (Integer) deptIdObj;
        }
        return null;
    }

    /**
     * Extracts the logged-in user's ID from the session.
     */
    public static Integer getSessionUserId(HttpSession session) {
        if (session == null) return null;
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        return null;
    }

    /**
     * Validates whether the logged-in user has permission to access a department's resource.
     * @param session Current HTTP session
     * @param targetDepartmentId Department ID associated with the requested notice/file/query
     * @return true if access is allowed, false if blocked
     */
    public static boolean hasDepartmentAccess(HttpSession session, Integer targetDepartmentId) {
        if (session == null || targetDepartmentId == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");
        
        // Admin has global administration rights
        if ("ADMIN".equalsIgnoreCase(role)) {
            return true;
        }

        Integer userDeptId = getSessionDepartmentId(session);
        if (userDeptId == null) {
            return false;
        }

        // Strict department match
        return userDeptId.equals(targetDepartmentId);
    }

    /**
     * Validates whether a teacher owns the specified resource (notice/file)
     * so they can edit or delete it.
     * @param session Current HTTP session
     * @param resourceTeacherId ID of teacher who created the resource
     * @return true if logged-in teacher is the owner
     */
    public static boolean isResourceOwner(HttpSession session, int resourceTeacherId) {
        if (session == null) return false;
        Integer userId = getSessionUserId(session);
        return userId != null && userId == resourceTeacherId;
    }
}
