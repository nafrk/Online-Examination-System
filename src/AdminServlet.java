import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final AdminManager adminManager = new AdminManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idStr = req.getParameter("id");
        String password = req.getParameter("password");

        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect("index.html?error=missingId");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect("index.html?error=badId");
            return;
        }

        if (password == null) {
            password = "";
        }

        boolean ok = adminManager.loginAdmin(id, password);
        if (ok) {
            HttpSession session = req.getSession(true);
            session.setAttribute("adminId", id);
            resp.sendRedirect("admin.html");
        } else {
            resp.sendRedirect("index.html?error=login");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("index.html");
    }
}
