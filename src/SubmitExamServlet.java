import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/submit-exam")
public class SubmitExamServlet extends HttpServlet {

    private final ExamTakingManager examTakingManager = new ExamTakingManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        int studentId = (Integer) session.getAttribute("studentId");

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

        Map<Integer, String> answers = new HashMap<>();
        for (String name : req.getParameterMap().keySet()) {
            if (name.startsWith("q_")) {
                try {
                    int qid = Integer.parseInt(name.substring(2));
                    String value = req.getParameter(name);
                    answers.put(qid, value != null ? value : "");
                } catch (NumberFormatException ignored) { }
            }
        }

        int score = examTakingManager.gradeExam(studentId, examId, answers);

        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            if (score >= 0) {
                out.print("{\"ok\":true,\"score\":" + score + "}");
            } else {
                out.print("{\"ok\":false}");
            }
        }
    }
}
