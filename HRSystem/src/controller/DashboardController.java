package controller;

import dao.DashboardDAO;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class DashboardController extends HttpServlet {
    private final DashboardDAO dao = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
        if (!SessionUtil.isAdmin(req))    { resp.sendRedirect(req.getContextPath() + "/employee/mypage"); return; }

        try {
            String month = YearMonth.now().toString();

            req.setAttribute("activeCount",     dao.countActiveEmployees());
            req.setAttribute("todayAttCount",   dao.countTodayAttendance());
            req.setAttribute("pendingLeaveCount", dao.countPendingLeaves());
            req.setAttribute("monthNetPay",     dao.getMonthTotalNetPay(month));
            req.setAttribute("pendingLeaves",   dao.getRecentPendingLeaves());
            req.setAttribute("attSummary",      dao.getMonthAttendanceSummary(month));
            req.setAttribute("deptHeadcount",   dao.getDeptHeadcount());
            req.setAttribute("month",           month);

            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
