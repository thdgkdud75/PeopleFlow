package util;

import model.Employee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionUtil {

    public static Employee getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (Employee) session.getAttribute("loginUser");
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoginUser(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        Employee user = getLoginUser(request);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public static void redirectIfNotLoggedIn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/auth/login.jsp");
        }
    }

    public static void redirectIfNotAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/employee/mypage.jsp");
        }
    }

    public static void setLoginUser(HttpServletRequest request, Employee employee) {
        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", employee);
        session.setMaxInactiveInterval(60 * 60); // 1시간
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }
}
