package controller;

import dao.PositionDAO;
import model.Position;
import util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/position/*")
public class PositionController extends HttpServlet {
    private final PositionDAO dao = new PositionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isAdmin(req)) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return; }
        String path = req.getPathInfo();

        try {
            if ("/list".equals(path)) {
                req.setAttribute("posList", dao.findAll());
                req.getRequestDispatcher("/WEB-INF/views/admin/position/list.jsp").forward(req, resp);
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
            if ("/create".equals(path)) {
                Position pos = buildPosition(req);
                dao.insert(pos);

            } else if ("/update".equals(path)) {
                Position pos = buildPosition(req);
                pos.setPosId(Integer.parseInt(req.getParameter("posId")));
                dao.update(pos);

            } else if ("/delete".equals(path)) {
                int posId = Integer.parseInt(req.getParameter("posId"));
                if (dao.countEmployees(posId) > 0) {
                    resp.sendRedirect(req.getContextPath() + "/position/list?error=hasEmployees");
                    return;
                }
                dao.delete(posId);
            }
            resp.sendRedirect(req.getContextPath() + "/position/list");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    private Position buildPosition(HttpServletRequest req) {
        Position pos = new Position();
        pos.setPosName(req.getParameter("posName"));
        pos.setLevel(Integer.parseInt(req.getParameter("level")));
        pos.setBaseSalary(Long.parseLong(req.getParameter("baseSalary")));
        return pos;
    }
}
