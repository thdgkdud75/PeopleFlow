package controller;

import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dept/*")
public class DeptController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
