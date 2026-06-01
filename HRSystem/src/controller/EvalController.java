package controller;

import model.Employee;
import model.Evaluation;
import service.EvalService;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/eval/*")
public class EvalController extends HttpServlet {
    private final EvalService service = new EvalService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login.jsp"); return; }
        String path = req.getPathInfo();

        try {
            if ("/list".equals(path)) {
                String period = req.getParameter("period");
                List<Evaluation> list = (period != null && !period.isBlank())
                    ? service.getEvalsByPeriod(period)
                    : service.getAllEvals();
                req.setAttribute("evalList", list);
                req.setAttribute("period", period != null ? period : "");
                req.getRequestDispatcher("/WEB-INF/views/admin/eval/list.jsp").forward(req, resp);
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
        Employee user = SessionUtil.getLoginUser(req);

        try {
            if ("/create".equals(path)) {
                Evaluation eval = new Evaluation();
                eval.setEmpId(Integer.parseInt(req.getParameter("empId")));
                eval.setEvalPeriod(req.getParameter("evalPeriod"));
                eval.setScore(Integer.parseInt(req.getParameter("score")));
                eval.setGrade(req.getParameter("grade"));
                eval.setComments(req.getParameter("comments"));
                eval.setWorkSummary(req.getParameter("workSummary"));
                eval.setEvaluatorId(user.getEmpId());
                service.createEvaluation(eval);
                resp.sendRedirect(req.getContextPath() + "/eval/list");
            }
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }
}
