package controller;

import model.Employee;
import service.AIService;
import service.EvalService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ai/*")
public class AIController extends HttpServlet {
    private final AIService  service  = new AIService();
    private final EvalService evalSvc = new EvalService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) { resp.sendError(401); return; }
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        Employee user = SessionUtil.getLoginUser(req);

        try {
            // 1번: HR 챗봇 (JSON 응답)
            if ("/chatbot".equals(path)) {
                resp.setContentType("application/json; charset=UTF-8");
                String message = req.getParameter("message");
                String answer  = service.chatbot(message, user.getName());
                try (PrintWriter out = resp.getWriter()) {
                    out.print("{\"answer\":\"" + escapeJson(answer) + "\"}");
                }

            // 4번: AI 근태 분석 (관리자 전용)
            } else if ("/attendance-analysis".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendError(403); return; }
                String month  = req.getParameter("month");
                String report = service.analyzeAttendance(month);
                req.setAttribute("aiReport", report);
                req.setAttribute("month", month);
                req.getRequestDispatcher("/WEB-INF/views/admin/attendance/ai_report.jsp").forward(req, resp);

            // 5번: AI 평가서 생성 (관리자 전용)
            } else if ("/eval-report".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendError(403); return; }
                int    evalId = Integer.parseInt(req.getParameter("evalId"));
                String report = service.generateEvalReport(evalId);
                req.setAttribute("aiReport",  report);
                req.setAttribute("evalId",    evalId);
                req.setAttribute("evalList",  evalSvc.getAllEvals());
                req.setAttribute("period",    "");
                req.getRequestDispatcher("/WEB-INF/views/admin/eval/list.jsp").forward(req, resp);

            // 5번: AI 평가서 저장
            } else if ("/eval-report/save".equals(path)) {
                if (!SessionUtil.isAdmin(req)) { resp.sendError(403); return; }
                int    evalId   = Integer.parseInt(req.getParameter("evalId"));
                String aiReport = req.getParameter("aiReport");
                evalSvc.saveAiReport(evalId, aiReport);
                resp.sendRedirect(req.getContextPath() + "/eval/list");
            }

        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}
