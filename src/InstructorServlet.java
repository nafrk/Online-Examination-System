import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/instructor")
public class InstructorServlet extends HttpServlet {

    private final InstructorManager instructorManager = new InstructorManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String mode = req.getParameter("mode"); // "login" or "register"
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
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

        if ("register".equalsIgnoreCase(mode)) {
            boolean ok = instructorManager.registerInstructor(id, name, password);
            if (ok) {
                resp.sendRedirect("index.html?registered=1");
            } else {
                resp.sendRedirect("index.html?error=register");
            }
        } else { // default to login
            boolean ok = instructorManager.loginInstructor(id, password);
            if (ok) {
                HttpSession session = req.getSession(true);
                session.setAttribute("instructorId", id);
                resp.sendRedirect("instructor.html");
            } else {
                resp.sendRedirect("index.html?error=login");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("index.html");
    }
}
