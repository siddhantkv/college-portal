package com.college.servlet;

import com.college.dao.FileDAO;
import com.college.model.FileData;
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
 * Controller servlet for File Upload and Deletion.
 * Enforces:
 * 1. Role: Only TEACHER can upload/delete documents.
 * 2. Allowed extensions: PDF, DOC, DOCX, PPT, PPTX, JPG, JPEG, PNG.
 * 3. File size limit: 10 MB.
 * 4. Unique file renaming to prevent accidental overwrites or path traversal attacks.
 */
@WebServlet("/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB memory buffer
        maxFileSize = 1024 * 1024 * 10,       // 10MB max per file
        maxRequestSize = 1024 * 1024 * 15     // 15MB max request
)
public class UploadServlet extends HttpServlet {

    private final FileDAO fileDAO = new FileDAO();

    // Allowed file extensions (Documents, Presentations, Spreadsheets, Images, Archives, Text)
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "csv",
            "jpg", "jpeg", "png", "webp", "gif", "svg", "bmp", "zip", "rar"
    );

    // Persistent storage directory in user home
    public static String getUploadDir() {
        String path = System.getProperty("user.home") + File.separator + "college_portal_uploads";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer userId = AccessControlUtil.getSessionUserId(session);

        String action = request.getParameter("action");

        // 1. DELETE ACTION (Teacher only - must own file)
        if ("delete".equalsIgnoreCase(action)) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
                return;
            }

            int fileId = parseIntSafe(request.getParameter("id"));
            FileData file = fileDAO.getFileById(fileId);

            if (file == null) {
                response.sendRedirect(request.getContextPath() + "/files?error=notfound");
                return;
            }

            // Security check: teacher must own the file and belong to the department
            if (!AccessControlUtil.isResourceOwner(session, file.getTeacherId()) 
                    || !AccessControlUtil.hasDepartmentAccess(session, file.getDepartmentId())) {
                response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
                return;
            }

            // Delete physical file from disk
            File diskFile = new File(getUploadDir(), file.getFileName());
            if (diskFile.exists()) {
                diskFile.delete();
            }

            // Delete database record
            fileDAO.deleteFile(fileId, userId);
            response.sendRedirect(request.getContextPath() + "/files?status=deleted");
            return;
        }

        // 2. SHOW UPLOAD FORM
        if (!"TEACHER".equalsIgnoreCase(role)) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
            return;
        }
        request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String role = (String) session.getAttribute("role");
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);
        Integer userId = AccessControlUtil.getSessionUserId(session);

        // Security check: Teachers only
        if (!"TEACHER".equalsIgnoreCase(role) || departmentId == null || userId == null) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=role");
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        Part filePart = request.getPart("file");

        // Validate text fields
        if (title == null || title.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Document Title is required!");
            request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
            return;
        }

        // Validate file presence
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("errorMessage", "Please select a file to upload!");
            request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
            return;
        }

        // Validate file size (10 MB = 10 * 1024 * 1024 bytes)
        long fileSize = filePart.getSize();
        if (fileSize > 10 * 1024 * 1024) {
            request.setAttribute("errorMessage", "File size exceeds 10 MB limit!");
            request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
            return;
        }

        // Extract and sanitize filename
        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String fileExt = "";
        int dotIndex = submittedFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExt = submittedFileName.substring(dotIndex + 1).toLowerCase();
        }

        // Validate file extension
        if (!ALLOWED_EXTENSIONS.contains(fileExt)) {
            request.setAttribute("errorMessage", "Invalid file format! Allowed: PDF, Word, PPT, Excel, Images (JPG, PNG, WEBP), ZIP, TXT.");
            request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
            return;
        }

        // Generate unique filename to prevent collisions and sanitize special chars
        String cleanOriginalName = submittedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String uniqueFileName = UUID.randomUUID().toString() + "_" + cleanOriginalName;

        // Save physical file to storage directory
        String uploadDir = getUploadDir();
        File targetFile = new File(uploadDir, uniqueFileName);
        filePart.write(targetFile.getAbsolutePath());

        // Save file metadata to database
        FileData fileData = new FileData(
                title.trim(),
                description != null ? description.trim() : "",
                uniqueFileName,
                submittedFileName,
                fileSize,
                fileExt,
                userId,
                departmentId
        );

        boolean saved = fileDAO.saveFile(fileData);

        if (saved) {
            response.sendRedirect(request.getContextPath() + "/files?status=uploaded");
        } else {
            // Cleanup file from disk if database insert failed
            if (targetFile.exists()) {
                targetFile.delete();
            }
            request.setAttribute("errorMessage", "Database error while saving file information.");
            request.getRequestDispatcher("/upload-form.jsp").forward(request, response);
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
