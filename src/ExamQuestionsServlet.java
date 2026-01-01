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

@WebServlet("/exam-questions")
public class ExamQuestionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String examIdStr = req.getParameter("examId");
        if (examIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int examId;
        try {
            examId = Integer.parseInt(examIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");

        try (Connection con = DBConnection.getConnection();
             PrintWriter out = resp.getWriter()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT question_id, question FROM question WHERE exam_id=? ORDER BY question_id"
            );
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(',');
                first = false;
                json.append('{')
                    .append("\"id\":").append(rs.getInt("question_id")).append(',')
                    .append("\"text\":\"").append(escape(rs.getString("question"))).append("\"")
                    .append('}');
            }
            json.append(']');

            out.print(json.toString());

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
