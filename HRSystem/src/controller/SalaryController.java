package controller;

import model.Employee;
import model.Salary;
import service.SalaryService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

@WebServlet("/salary/*")
public class SalaryController extends HttpServlet {
    private final SalaryService service = new SalaryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
        String path = req.getPathInfo();

        try {
            if ("/my".equals(path)) {
                Employee user = SessionUtil.getLoginUser(req);
                List<Salary> list = service.getMySalaries(user.getEmpId());
                req.setAttribute("salaryList", list);
                req.getRequestDispatcher("/WEB-INF/views/employee/salary.jsp").forward(req, resp);

            } else if ("/list".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendError(403); return; }
                String month = req.getParameter("month");
                if (month == null || month.isBlank()) month = YearMonth.now().toString();

                List<Salary> list = service.getMonthSalaries(month);

                // 합계 계산
                long totalBase = 0, totalAllow = 0, totalDeduct = 0, totalNet = 0;
                int pendingCount = 0;
                for (Salary s : list) {
                    totalBase   += s.getBasePay();
                    totalAllow  += s.getAllowance();
                    totalDeduct += s.getDeduction();
                    totalNet    += s.getNetPay();
                    if ("PENDING".equals(s.getPayStatus())) pendingCount++;
                }

                req.setAttribute("salaryList",   list);
                req.setAttribute("month",         month);
                req.setAttribute("totalBase",     totalBase);
                req.setAttribute("totalAllow",    totalAllow);
                req.setAttribute("totalDeduct",   totalDeduct);
                req.setAttribute("totalNet",      totalNet);
                req.setAttribute("pendingCount",  pendingCount);
                req.getRequestDispatcher("/WEB-INF/views/admin/salary/list.jsp").forward(req, resp);
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
            String month = req.getParameter("month");
            if (month == null || month.isBlank()) month = YearMonth.now().toString();

            if ("/generate".equals(path)) {
                // 직급 기본급 기반 일괄 생성
                int count = service.generateMonthlySalaries(month);
                req.getSession().setAttribute("msg", count + "명의 급여가 생성되었습니다.");

            } else if ("/create".equals(path)) {
                // 개별 수동 등록 (기존 호환)
                Salary sal = new Salary();
                sal.setEmpId(Integer.parseInt(req.getParameter("empId")));
                sal.setSalMonth(month);
                sal.setBasePay(Long.parseLong(req.getParameter("basePay")));
                sal.setAllowance(Long.parseLong(req.getParameter("allowance")));
                sal.setDeduction(Long.parseLong(req.getParameter("deduction")));
                service.createSalary(sal);

            } else if ("/pay".equals(path)) {
                // 개별 지급 확정
                int salId = Integer.parseInt(req.getParameter("salId"));
                service.confirmPayment(salId);

            } else if ("/pay-all".equals(path)) {
                // 전체 지급 확정
                int count = service.confirmAllPayments(month);
                req.getSession().setAttribute("msg", count + "건이 지급 확정되었습니다.");
            }

            resp.sendRedirect(req.getContextPath() + "/salary/list?month=" + month);
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
