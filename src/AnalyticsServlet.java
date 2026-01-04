import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/analytics-data")
public class AnalyticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int studentId = (Integer) session.getAttribute("studentId");

        resp.setContentType("application/json;charset=UTF-8");

        try (Connection con = DBConnection.getConnection();
             PrintWriter out = resp.getWriter()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) AS total_exams, AVG(score) AS avg_score, MAX(score) AS max_score " +
                "FROM result WHERE student_id=?"
            );
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            int total = 0;
            double avg = 0.0;
            int max = 0;
            if (rs.next()) {
                total = rs.getInt("total_exams");
                avg = rs.getDouble("avg_score");
                max = rs.getInt("max_score");
            }

            String json = String.format("{\"totalExams\":%d,\"avgScore\":%.2f,\"maxScore\":%d}",
                    total, avg, max);
            out.print(json);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
