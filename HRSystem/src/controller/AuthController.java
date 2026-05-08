package controller;

import model.Employee;
import service.EmployeeService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/auth/login", "/auth/logout"})
public class AuthController extends HttpServlet {
    private final EmployeeService service = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();

        if ("/auth/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/auth/logout".equals(path)) {
            handleLogout(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String empNo = req.getParameter("empNo");
        String password = req.getParameter("password");

        try {
            Employee emp = service.login(empNo, password);
            if (emp == null) {
                resp.sendRedirect(req.getContextPath() + "/auth/login.jsp?error=1");
                return;
            }
            SessionUtil.setLoginUser(req, emp);
            if ("ADMIN".equals(emp.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/employee/mypage");
            }
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/auth/login.jsp?error=2");
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        SessionUtil.invalidate(req);
        resp.sendRedirect(req.getContextPath() + "/auth/login.jsp");
    }
}
