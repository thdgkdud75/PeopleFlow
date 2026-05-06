package controller;

import model.Employee;
import model.Salary;
import service.SalaryService;
import util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/salary/*")
public class SalaryController extends HttpServlet {
    private final SalaryService service = new SalaryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        String path = req.getPathInfo();

        try {
            if ("/my".equals(path)) {
                Employee user = SessionUtil.getLoginUser(req);
                List<Salary> list = service.getMySalaries(user.getEmpId());
                req.setAttribute("salaryList", list);
                req.getRequestDispatcher("/employee/salary.jsp").forward(req, resp);
            } else if ("/list".equals(path)) {
                if (!SessionUtil.isAdmin(req)) return;
                String month = req.getParameter("month");
                List<Salary> list = service.getMonthSalaries(month != null ? month : java.time.YearMonth.now().toString());
                req.setAttribute("salaryList", list);
                req.setAttribute("month", month);
                req.getRequestDispatcher("/admin/salary/list.jsp").forward(req, resp);
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
            if ("/create".equals(path)) {
                Salary sal = new Salary();
                sal.setEmpId(Integer.parseInt(req.getParameter("empId")));
                sal.setSalMonth(req.getParameter("salMonth"));
                sal.setBasePay(Long.parseLong(req.getParameter("basePay")));
                sal.setAllowance(Long.parseLong(req.getParameter("allowance")));
                sal.setDeduction(Long.parseLong(req.getParameter("deduction")));
                service.createSalary(sal);
            } else if ("/pay".equals(path)) {
                int salId = Integer.parseInt(req.getParameter("salId"));
                service.confirmPayment(salId);
            }
            resp.sendRedirect(req.getContextPath() + "/salary/list");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
