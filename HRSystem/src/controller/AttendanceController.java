package controller;

import model.Attendance;
import model.Employee;
import service.AttendanceService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/attendance/*")
public class AttendanceController extends HttpServlet {
    private final AttendanceService service = new AttendanceService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        String path = req.getPathInfo();

        try {
            if ("/my".equals(path)) {
                Employee user = SessionUtil.getLoginUser(req);
                List<Attendance> list = service.getMyAttendance(user.getEmpId());
                req.setAttribute("attendanceList", list);
                req.getRequestDispatcher("/employee/attendance.jsp").forward(req, resp);
            } else if ("/list".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/employee/mypage.jsp"); return; }
                String month = req.getParameter("month");
                List<Attendance> list = service.getMonthlyAttendance(month != null ? month : java.time.YearMonth.now().toString());
                req.setAttribute("attendanceList", list);
                req.setAttribute("month", month);
                req.getRequestDispatcher("/admin/attendance/list.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        String path = req.getPathInfo();
        Employee user = SessionUtil.getLoginUser(req);

        try {
            if ("/checkin".equals(path)) {
                service.checkIn(user.getEmpId());
            } else if ("/checkout".equals(path)) {
                service.checkOut(user.getEmpId());
            }
            resp.sendRedirect(req.getContextPath() + "/attendance/my");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
