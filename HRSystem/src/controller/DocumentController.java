package controller;

import model.BusinessTrip;
import model.Employee;
import model.MeetingMinutes;
import model.WorkLog;
import service.DocumentService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/doc/*")
public class DocumentController extends HttpServlet {

    private final DocumentService service = new DocumentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        Employee user = SessionUtil.getLoginUser(req);

        try {
            switch (path) {
                case "/worklog" -> {
                    req.setAttribute("logs", service.getMyWorkLogs(user.getEmpId()));
                    req.getRequestDispatcher("/WEB-INF/views/employee/doc/worklog.jsp").forward(req, resp);
                }
                case "/meeting" -> {
                    req.setAttribute("meetings", service.getMyMeetings(user.getEmpId()));
                    req.getRequestDispatcher("/WEB-INF/views/employee/doc/meeting.jsp").forward(req, resp);
                }
                case "/trip" -> {
                    req.setAttribute("trips", service.getMyTrips(user.getEmpId()));
                    req.getRequestDispatcher("/WEB-INF/views/employee/doc/trip.jsp").forward(req, resp);
                }
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendError(401); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        Employee user = SessionUtil.getLoginUser(req);

        try {
            switch (path) {
                case "/worklog/save" -> {
                    WorkLog w = new WorkLog();
                    w.setEmpId(user.getEmpId());
                    w.setLogDate(LocalDate.parse(req.getParameter("logDate")));
                    w.setContent(req.getParameter("content"));
                    service.saveWorkLog(w);
                    resp.sendRedirect(req.getContextPath() + "/doc/worklog");
                }
                case "/meeting/save" -> {
                    MeetingMinutes m = new MeetingMinutes();
                    m.setEmpId(user.getEmpId());
                    m.setMeetingDate(LocalDate.parse(req.getParameter("meetingDate")));
                    m.setTitle(req.getParameter("title"));
                    m.setAttendees(req.getParameter("attendees"));
                    m.setContent(req.getParameter("content"));
                    service.saveMeeting(m);
                    resp.sendRedirect(req.getContextPath() + "/doc/meeting");
                }
                case "/trip/save" -> {
                    BusinessTrip t = new BusinessTrip();
                    t.setEmpId(user.getEmpId());
                    t.setTripStart(LocalDate.parse(req.getParameter("tripStart")));
                    t.setTripEnd(LocalDate.parse(req.getParameter("tripEnd")));
                    t.setDestination(req.getParameter("destination"));
                    t.setPurpose(req.getParameter("purpose"));
                    t.setContent(req.getParameter("content"));
                    service.saveTrip(t);
                    resp.sendRedirect(req.getContextPath() + "/doc/trip");
                }
                case "/worklog/summarize" -> {
                    int logId = Integer.parseInt(req.getParameter("logId"));
                    service.summarizeWorkLog(logId);
                    resp.sendRedirect(req.getContextPath() + "/doc/worklog");
                }
                case "/meeting/summarize" -> {
                    int meetingId = Integer.parseInt(req.getParameter("meetingId"));
                    service.summarizeMeeting(meetingId);
                    resp.sendRedirect(req.getContextPath() + "/doc/meeting");
                }
                case "/trip/summarize" -> {
                    int tripId = Integer.parseInt(req.getParameter("tripId"));
                    service.summarizeTrip(tripId);
                    resp.sendRedirect(req.getContextPath() + "/doc/trip");
                }
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
