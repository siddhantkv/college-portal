package com.college.servlet;

import com.college.dao.NoticeDAO;
import com.college.model.Notice;
import com.college.util.AccessControlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller servlet for Department Notices.
 * Teachers: Post, Edit, and Delete notices for their department.
 * Supports optional image/document attachments (e.g., circulars, timetables).
 * Students: View latest notices of their own department only.
 */
@WebServlet("/notices")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB memory buffer
        maxFileSize = 1024 * 1024 * 10,       // 10MB max per attachment
        maxRequestSize = 1024 * 1024 * 15     // 15MB max request
)
public class NoticeServlet extends HttpServlet {

    private final NoticeDAO noticeDAO = new NoticeDAO();

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt",
            "jpg", "jpeg", "png", "webp", "gif", "svg", "bmp"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);
        Integer userId = AccessControlUtil.getSessionUserId(session);

        String action = request.getParameter("action");

        // 1. ADD NOTICE FORM (Teachers Only)
        if ("add".equalsIgnoreCase(action)) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }
            request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            return;
        }

        // 2. EDIT NOTICE FORM (Teachers Only - must own the notice)
        if ("edit".equalsIgnoreCase(action)) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }

            int noticeId = parseIntSafe(request.getParameter("id"));
            Notice notice = noticeDAO.getNoticeById(noticeId);

            // Backend Check: Notice must exist, belong to same dept, and owned by this teacher
            if (notice == null) {
                response.sendRedirect(request.getContextPath() + "/notices?error=notfound");
                return;
            }
            if (!AccessControlUtil.hasDepartmentAccess(session, notice.getDepartmentId()) 
                    || !AccessControlUtil.isResourceOwner(session, notice.getTeacherId())) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
                return;
            }

            request.setAttribute("notice", notice);
            request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            return;
        }

        // 3. DELETE NOTICE (Teachers Only - must own the notice)
        if ("delete".equalsIgnoreCase(action)) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }

            int noticeId = parseIntSafe(request.getParameter("id"));
            Notice notice = noticeDAO.getNoticeById(noticeId);

            if (notice == null) {
                response.sendRedirect(request.getContextPath() + "/notices?error=notfound");
                return;
            }
            if (!AccessControlUtil.hasDepartmentAccess(session, notice.getDepartmentId()) 
                    || !AccessControlUtil.isResourceOwner(session, notice.getTeacherId())) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
                return;
            }

            // Remove physical attachment if exists
            if (notice.getAttachmentName() != null) {
                File diskFile = new File(UploadServlet.getUploadDir(), notice.getAttachmentName());
                if (diskFile.exists()) {
                    diskFile.delete();
                }
            }

            noticeDAO.deleteNotice(noticeId, userId);
            response.sendRedirect(request.getContextPath() + "/notices?status=deleted");
            return;
        }

        // 4. DEFAULT: VIEW NOTICES (Filtered strictly by session department)
        if (departmentId != null) {
            List<Notice> notices = noticeDAO.getNoticesByDepartment(departmentId);
            request.setAttribute("notices", notices);
        }

        request.getRequestDispatcher("/notices.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);
        Integer userId = AccessControlUtil.getSessionUserId(session);

        // Security check: Only Teachers can create or update notices
        if (!"TEACHER".equalsIgnoreCase(role) || departmentId == null || userId == null) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
            return;
        }

        String idStr = request.getParameter("id");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        Part attachmentPart = null;
        try {
            attachmentPart = request.getPart("attachment");
        } catch (Exception ignored) {}

        int noticeId = parseIntSafe(idStr);

        // Server-side validation
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Title and Notice content cannot be empty!");
            if (noticeId > 0) {
                Notice n = new Notice();
                n.setId(noticeId);
                n.setTitle(title);
                n.setContent(content);
                request.setAttribute("notice", n);
            }
            request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            return;
        }

        if (title.length() > 200) {
            request.setAttribute("errorMessage", "Notice title must be under 200 characters!");
            request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            return;
        }

        // Handle optional file/image upload
        String uniqueFileName = null;
        String originalFileName = null;

        if (attachmentPart != null && attachmentPart.getSize() > 0) {
            if (attachmentPart.getSize() > 10 * 1024 * 1024) {
                request.setAttribute("errorMessage", "Notice attachment exceeds 10 MB limit!");
                request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
                return;
            }

            originalFileName = Paths.get(attachmentPart.getSubmittedFileName()).getFileName().toString();
            int dotIndex = originalFileName.lastIndexOf('.');
            String ext = dotIndex > 0 ? originalFileName.substring(dotIndex + 1).toLowerCase() : "";

            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                request.setAttribute("errorMessage", "Invalid attachment format! Allowed: Images (JPG, PNG, WEBP, GIF), PDF, Word, PPT, Excel, TXT.");
                request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
                return;
            }

            String clean = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            uniqueFileName = "notice_" + UUID.randomUUID().toString() + "_" + clean;

            File targetFile = new File(UploadServlet.getUploadDir(), uniqueFileName);
            attachmentPart.write(targetFile.getAbsolutePath());
        }

        if (noticeId == 0) {
            // Create New Notice
            Notice newNotice = new Notice(title.trim(), content.trim(), userId, departmentId, uniqueFileName, originalFileName);
            boolean created = noticeDAO.addNotice(newNotice);
            if (created) {
                response.sendRedirect(request.getContextPath() + "/notices?status=created");
            } else {
                request.setAttribute("errorMessage", "Failed to publish notice. Database error.");
                request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            }
        } else {
            // Update Existing Notice
            Notice existing = noticeDAO.getNoticeById(noticeId);
            if (existing == null || !AccessControlUtil.isResourceOwner(session, existing.getTeacherId()) 
                    || !AccessControlUtil.hasDepartmentAccess(session, existing.getDepartmentId())) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
                return;
            }

            existing.setTitle(title.trim());
            existing.setContent(content.trim());
            if (uniqueFileName != null) {
                // If existing had an old attachment, delete old file
                if (existing.getAttachmentName() != null) {
                    File oldFile = new File(UploadServlet.getUploadDir(), existing.getAttachmentName());
                    if (oldFile.exists()) oldFile.delete();
                }
                existing.setAttachmentName(uniqueFileName);
                existing.setOriginalAttachmentName(originalFileName);
            }

            boolean updated = noticeDAO.updateNotice(existing);
            if (updated) {
                response.sendRedirect(request.getContextPath() + "/notices?status=updated");
            } else {
                request.setAttribute("errorMessage", "Failed to update notice.");
                request.setAttribute("notice", existing);
                request.getRequestDispatcher("/notice-form.jsp").forward(request, response);
            }
        }
    }

    private int parseIntSafe(String val) {
        if (val == null) return 0;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
