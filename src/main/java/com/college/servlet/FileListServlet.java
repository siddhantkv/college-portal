package com.college.servlet;

import com.college.dao.FileDAO;
import com.college.model.FileData;
import com.college.util.AccessControlUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller servlet to list study materials and documents.
 * Enforces strict department isolation: only files belonging to session department are loaded.
 */
@WebServlet("/files")
public class FileListServlet extends HttpServlet {

    private final FileDAO fileDAO = new FileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer departmentId = AccessControlUtil.getSessionDepartmentId(session);

        if (departmentId != null) {
            List<FileData> files = fileDAO.getFilesByDepartment(departmentId);
            request.setAttribute("files", files);
        }

        request.getRequestDispatcher("/files.jsp").forward(request, response);
    }
}
