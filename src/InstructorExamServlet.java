import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/instructor-exam")
public class InstructorExamServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("instructorId") == null) {
            resp.sendRedirect("index.html");
            return;
        }

        String title = req.getParameter("title");
        String durationStr = req.getParameter("duration");

        if (title == null || title.trim().isEmpty() || durationStr == null || durationStr.trim().isEmpty()) {
            resp.sendRedirect("instructor.html?error=exam");
            return;
        }

        int duration;
        try {
            duration = Integer.parseInt(durationStr.trim());
            if (duration <= 0) {
                resp.sendRedirect("instructor.html?error=exam");
                return;
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect("instructor.html?error=exam");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                resp.sendRedirect("instructor.html?error=exam");
                return;
            }

            // Insert a new exam row. We don't depend on status or instructor_id columns.
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO exam (title, duration) VALUES (?, ?)"
            );
            ps.setString(1, title.trim());
            ps.setInt(2, duration);
            ps.executeUpdate();

            resp.sendRedirect("instructor.html?examCreated=1");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("instructor.html?error=exam");
        }
    }
}
