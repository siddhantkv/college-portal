package com.college.servlet;

import com.college.dao.FileDAO;
import com.college.dao.NoticeDAO;
import com.college.model.FileData;
import com.college.model.Notice;
import com.college.util.AccessControlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Controller servlet for securely downloading and viewing department documents and notice attachments.
 * Security Features:
 * 1. Validates department match with user session.
 * 2. Supports 'type=notice' for downloading or viewing notice circular images/PDFs.
 * 3. Supports 'view=inline' to view images directly inside the page.
 */
@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    private final FileDAO fileDAO = new FileDAO();
    private final NoticeDAO noticeDAO = new NoticeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);

        String idStr = request.getParameter("id");
        String type = request.getParameter("type");   // "notice" or default null (study material)
        String viewMode = request.getParameter("view"); // "inline" for browser display, or "attachment" (default)

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File ID missing");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file ID");
            return;
        }

        String storedFileName = null;
        String originalFileName = null;
        int targetDepartmentId = -1;

        if ("notice".equalsIgnoreCase(type)) {
            // Handle Notice Attachment
            Notice notice = noticeDAO.getNoticeById(id);
            if (notice == null || !notice.hasAttachment()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Notice attachment not found.");
                return;
            }
            storedFileName = notice.getAttachmentName();
            originalFileName = notice.getOriginalAttachmentName();
            targetDepartmentId = notice.getDepartmentId();
        } else {
            // Handle Study Material File
            FileData fileData = fileDAO.getFileById(id);
            if (fileData == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested file does not exist.");
                return;
            }
            storedFileName = fileData.getFileName();
            originalFileName = fileData.getOriginalName();
            targetDepartmentId = fileData.getDepartmentId();
        }

        // Strict Department Isolation Security Check
        if (!AccessControlUtil.hasDepartmentAccess(session, targetDepartmentId)) {
            response.sendRedirect(request.getContextPath() + "/access-denied.jsp?reason=department");
            return;
        }

        // Locate physical file on server
        File physicalFile = new File(UploadServlet.getUploadDir(), storedFileName);
        if (!physicalFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Physical file missing from server storage.");
            return;
        }

        // Set Response Headers
        String mimeType = getServletContext().getMimeType(originalFileName);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);
        response.setContentLengthLong(physicalFile.length());
        
        String safeOriginalName = originalFileName.replace("\"", "");
        String disposition = "inline".equalsIgnoreCase(viewMode) ? "inline" : "attachment";
        response.setHeader("Content-Disposition", disposition + "; filename=\"" + safeOriginalName + "\"");

        // Stream file content to user
        try (FileInputStream inStream = new FileInputStream(physicalFile);
             OutputStream outStream = response.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            outStream.flush();
        }
    }
}
