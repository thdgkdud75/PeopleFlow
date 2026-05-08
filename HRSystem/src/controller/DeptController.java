package controller;

import dao.DeptDAO;
import dao.EmployeeDAO;
import model.Department;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/dept/*")
public class DeptController extends HttpServlet {
    private final DeptDAO dao = new DeptDAO();
    private final EmployeeDAO empDao = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
        String path = req.getPathInfo();

        try {
            if ("/list".equals(path)) {
                req.setAttribute("deptList", dao.findAll());
                req.setAttribute("empList", empDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/dept/list.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        try {
            if ("/create".equals(path)) {
                Department dept = new Department();
                dept.setDeptName(req.getParameter("deptName"));
                dept.setManagerId(parseInt(req.getParameter("managerId")));
                dao.insert(dept);

            } else if ("/update".equals(path)) {
                Department dept = new Department();
                dept.setDeptId(Integer.parseInt(req.getParameter("deptId")));
                dept.setDeptName(req.getParameter("deptName"));
                dept.setManagerId(parseInt(req.getParameter("managerId")));
                dao.update(dept);

            } else if ("/delete".equals(path)) {
                int deptId = Integer.parseInt(req.getParameter("deptId"));
                if (dao.countEmployees(deptId) > 0) {
                    resp.sendRedirect(req.getContextPath() + "/dept/list?error=hasEmployees");
                    return;
                }
                dao.delete(deptId);
            }
            resp.sendRedirect(req.getContextPath() + "/dept/list");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private int parseInt(String val) {
        try { return Integer.parseInt(val); } catch (Exception e) { return 0; }
    }
}
