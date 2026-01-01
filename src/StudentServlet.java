import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {

    private final StudentManager studentManager = new StudentManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String mode = req.getParameter("mode"); // "login" or "register"
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String source = req.getParameter("source"); // e.g. "admin" when called from admin dashboard

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
            boolean ok = studentManager.registerStudent(id, name, password);
            if (ok) {
                if ("admin".equalsIgnoreCase(source)) {
                    resp.sendRedirect("admin.html?studentAdded=1");
                } else {
                    resp.sendRedirect("index.html?registered=1");
                }
            } else {
                if ("admin".equalsIgnoreCase(source)) {
                    resp.sendRedirect("admin.html?error=studentRegister");
                } else {
                    resp.sendRedirect("index.html?error=register");
                }
            }
        } else if ("login".equalsIgnoreCase(mode)) {
            boolean ok = studentManager.loginStudent(id, password);
            if (ok) {
                HttpSession session = req.getSession(true);
                session.setAttribute("studentId", id);
                resp.sendRedirect("student.html");
            } else {
                resp.sendRedirect("index.html?error=login");
            }
        } else {
            resp.sendRedirect("index.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("index.html");
    }
}
