package controller;

import model.Employee;
import model.LeaveRequest;
import service.LeaveService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet("/leave/*")
public class LeaveController extends HttpServlet {
    private final LeaveService service = new LeaveService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        String path = req.getPathInfo();

        try {
            if ("/history".equals(path)) {
                Employee user = SessionUtil.getLoginUser(req);
                List<LeaveRequest> list = service.getMyLeaves(user.getEmpId());
                req.setAttribute("leaveList", list);
                req.getRequestDispatcher("/employee/leave/history.jsp").forward(req, resp);
            } else if ("/list".equals(path)) {
                if (!SessionUtil.isAdmin(req)) return;
                List<LeaveRequest> list = service.getAllLeaves();
                req.setAttribute("leaveList", list);
                req.getRequestDispatcher("/admin/leave/list.jsp").forward(req, resp);
            } else if ("/pending".equals(path)) {
                if (!SessionUtil.isAdmin(req)) return;
                List<LeaveRequest> list = service.getPendingLeaves();
                req.setAttribute("leaveList", list);
                req.getRequestDispatcher("/admin/leave/approve.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        Employee user = SessionUtil.getLoginUser(req);

        try {
            if ("/apply".equals(path)) {
                LeaveRequest lr = new LeaveRequest();
                lr.setEmpId(user.getEmpId());
                lr.setLeaveType(req.getParameter("leaveType"));
                LocalDate start = LocalDate.parse(req.getParameter("startDate"));
                LocalDate end = LocalDate.parse(req.getParameter("endDate"));
                lr.setStartDate(start);
                lr.setEndDate(end);
                lr.setLeaveDays((int) ChronoUnit.DAYS.between(start, end) + 1);
                lr.setReason(req.getParameter("reason"));
                service.applyLeave(lr);
                resp.sendRedirect(req.getContextPath() + "/leave/history");
            } else if ("/approve".equals(path)) {
                if (!SessionUtil.isAdmin(req)) return;
                int leaveId = Integer.parseInt(req.getParameter("leaveId"));
                String action = req.getParameter("action");
                if ("APPROVE".equals(action)) service.approveLeave(leaveId, user.getEmpId());
                else service.rejectLeave(leaveId, user.getEmpId());
                resp.sendRedirect(req.getContextPath() + "/leave/pending");
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
