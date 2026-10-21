package com.college.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Authentication and Authorization Servlet Filter.
 * Intercepts all incoming requests to enforce:
 * 1. Login verification (blocks unauthorized guests from opening protected pages).
 * 2. Role-based URL protection (e.g. Student cannot access Admin or Teacher dashboard).
 * 3. Cache control headers (prevents viewing cached protected pages via browser Back button after logout).
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    // Publicly accessible paths that do NOT require login
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/",
            "/index.jsp",
            "/login",
            "/login.jsp",
            "/register",
            "/register.jsp",
            "/logout",
            "/access-denied.jsp"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get requested relative path within the web application
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // Normalize path (remove trailing slash if length > 1)
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        // 1. Allow Static Resources (CSS, JS, Images, Fonts)
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") 
                || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") 
                || path.endsWith(".jpg") || path.endsWith(".ico") || path.endsWith(".svg")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Allow Public Pages (Home, Login, Register, Logout)
        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Check Session Authentication for all protected pages
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (!isLoggedIn) {
            // Not logged in -> Redirect to login page
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=unauthorized");
            return;
        }

        // 4. Cache-Control Headers to prevent Back-button caching of protected pages
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        httpResponse.setHeader("Pragma", "no-cache");                                  // HTTP 1.0
        httpResponse.setDateHeader("Expires", 0);                                       // Proxies

        // 5. Role-based Route Protection
        String role = (String) session.getAttribute("role");

        // Admin Routes: only ADMIN
        if (path.startsWith("/admin") || path.contains("admin-dashboard")) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp?reason=role");
                return;
            }
        }

        // Teacher Routes: only TEACHER
        if (path.contains("teacher-dashboard")) {
            if (!"TEACHER".equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp?reason=role");
                return;
            }
        }

        // Student Routes: only STUDENT
        if (path.contains("student-dashboard")) {
            if (!"STUDENT".equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(contextPath + "/access-denied.jsp?reason=role");
                return;
            }
        }

        // User is authenticated and authorized -> Continue to requested servlet or JSP
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
