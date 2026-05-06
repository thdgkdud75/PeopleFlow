package controller;

import model.Employee;
import service.EmployeeService;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/employee/*")
public class EmployeeController extends HttpServlet {
    private final EmployeeService service = new EmployeeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();

        try {
            if ("/list".equals(path)) {
                List<Employee> list = service.getAllEmployees();
                req.setAttribute("employeeList", list);
                req.getRequestDispatcher("/admin/employee/list.jsp").forward(req, resp);
            } else if ("/detail".equals(path)) {
                int empId = Integer.parseInt(req.getParameter("empId"));
                Employee emp = service.getEmployee(empId);
                req.setAttribute("employee", emp);
                req.getRequestDispatcher("/admin/employee/detail.jsp").forward(req, resp);
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
