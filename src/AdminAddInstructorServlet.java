import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/admin-instructor")
public class AdminAddInstructorServlet extends HttpServlet {

    private final AdminManager adminManager = new AdminManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Only allow if an admin is logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect("admin.html?error=instructor");
            return;
        }

        try {
            int id = Integer.parseInt(idStr.trim());
            // Use existing AdminManager logic to pre-register an instructor ID
            adminManager.addInstructor(id);
            resp.sendRedirect("admin.html?instructorAdded=1");
        } catch (NumberFormatException e) {
            resp.sendRedirect("admin.html?error=instructor");
        }
    }
}
