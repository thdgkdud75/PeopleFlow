package controller;

import dao.DeptDAO;
import dao.PositionDAO;
import model.Employee;
import service.EmployeeService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/employee/*")
public class EmployeeController extends HttpServlet {
    private final EmployeeService service = new EmployeeService();
    private final DeptDAO deptDao = new DeptDAO();
    private final PositionDAO posDao = new PositionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        try {
            if ("/list".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
                String keyword = nvl(req.getParameter("keyword"));
                int filterDept = toInt(req.getParameter("deptId"));
                int filterPos  = toInt(req.getParameter("posId"));
                String status  = nvl(req.getParameter("status"));
                int page = Math.max(1, toIntDefault(req.getParameter("page"), 1));

                List<Employee> list = service.searchEmployees(keyword, filterDept, filterPos, status, page);
                int totalCount = service.countEmployees(keyword, filterDept, filterPos, status);
                int totalPages = (int) Math.ceil((double) totalCount / EmployeeService.PAGE_SIZE);

                req.setAttribute("employeeList", list);
                req.setAttribute("totalCount",   totalCount);
                req.setAttribute("totalPages",   Math.max(1, totalPages));
                req.setAttribute("currentPage",  page);
                req.setAttribute("keyword",      keyword);
                req.setAttribute("filterDept",   filterDept);
                req.setAttribute("filterPos",    filterPos);
                req.setAttribute("filterStatus", status);
                req.setAttribute("deptList", deptDao.findAll());
                req.setAttribute("posList",  posDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/employee/list.jsp").forward(req, resp);
            } else if ("/detail".equals(path)) {
                int empId = Integer.parseInt(req.getParameter("empId"));
                Employee emp = service.getEmployee(empId);
                req.setAttribute("employee", emp);
                req.setAttribute("deptList", deptDao.findAll());
                req.setAttribute("posList", posDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/employee/detail.jsp").forward(req, resp);
            } else if ("/mypage".equals(path)) {
                req.getRequestDispatcher("/WEB-INF/views/employee/mypage.jsp").forward(req, resp);
            } else if ("/chatbot".equals(path)) {
                req.getRequestDispatcher("/WEB-INF/views/employee/chatbot.jsp").forward(req, resp);
            } else if ("/register".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendError(403); return; }
                req.setAttribute("deptList", deptDao.findAll());
                req.setAttribute("posList", posDao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/employee/register.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        try {
            if ("/register".equals(path)) {
                Employee emp = buildEmployee(req);
                service.registerEmployee(emp);
                resp.sendRedirect(req.getContextPath() + "/employee/list");
            } else if ("/update".equals(path)) {
                Employee emp = buildEmployee(req);
                emp.setEmpId(Integer.parseInt(req.getParameter("empId")));
                service.updateEmployee(emp);
                resp.sendRedirect(req.getContextPath() + "/employee/list");
            } else if ("/delete".equals(path)) {
                int empId = Integer.parseInt(req.getParameter("empId"));
                service.deleteEmployee(empId);
                resp.sendRedirect(req.getContextPath() + "/employee/list");
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private String nvl(String v) { return v != null ? v.trim() : ""; }
    private int toInt(String v) { try { return Integer.parseInt(v); } catch (Exception e) { return 0; } }
    private int toIntDefault(String v, int def) { try { return Integer.parseInt(v); } catch (Exception e) { return def; } }

    private Employee buildEmployee(HttpServletRequest req) {
        Employee emp = new Employee();
        emp.setEmpNo(req.getParameter("empNo"));
        emp.setName(req.getParameter("name"));
        emp.setEmail(req.getParameter("email"));
        emp.setPhone(req.getParameter("phone"));
        emp.setDeptId(Integer.parseInt(req.getParameter("deptId")));
        emp.setPosId(Integer.parseInt(req.getParameter("posId")));
        emp.setHireDate(LocalDate.parse(req.getParameter("hireDate")));
        emp.setStatus(req.getParameter("status"));
        emp.setRole(req.getParameter("role") != null ? req.getParameter("role") : "EMPLOYEE");
        emp.setPassword(req.getParameter("password"));
        return emp;
    }
}
