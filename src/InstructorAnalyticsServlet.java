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

/**
 * Simple instructor performance analytics servlet.
 *
 * Because the current schema does not reliably track which instructor owns
 * each exam, this servlet reports global metrics that are still useful:
 *  - totalExams: number of exams in the system
 *  - bonusQuestions: number of questions marked as bonus
 */
@WebServlet("/instructor-analytics")
public class InstructorAnalyticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("instructorId") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");

        try (Connection con = DBConnection.getConnection();
             PrintWriter out = resp.getWriter()) {

            int totalExams = 0;
            int bonusQuestions = 0;

            // Total exams in the system
            PreparedStatement exams = con.prepareStatement(
                    "SELECT COUNT(*) AS total_exams FROM exam");
            ResultSet rs1 = exams.executeQuery();
            if (rs1.next()) {
                totalExams = rs1.getInt("total_exams");
            }

            // Total bonus questions approved
            PreparedStatement bonus = con.prepareStatement(
                    "SELECT COUNT(*) AS bonus_questions FROM question WHERE bonus=TRUE");
            ResultSet rs2 = bonus.executeQuery();
            if (rs2.next()) {
                bonusQuestions = rs2.getInt("bonus_questions");
            }

            String json = String.format("{\"totalExams\":%d,\"bonusQuestions\":%d}",
                    totalExams, bonusQuestions);
            out.print(json);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
