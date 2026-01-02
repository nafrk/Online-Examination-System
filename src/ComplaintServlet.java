import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/complaint")
public class ComplaintServlet extends HttpServlet {

    private final ComplaintManager complaintManager = new ComplaintManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            resp.sendRedirect("index.html");
            return;
        }
        int studentId = (Integer) session.getAttribute("studentId");

        String examIdStr = req.getParameter("examId");
        String questionIdStr = req.getParameter("questionId");
        String message = req.getParameter("message");

        if (examIdStr == null || questionIdStr == null || message == null || message.trim().isEmpty()) {
            resp.sendRedirect("complaint.html?status=error");
            return;
        }

        try {
            int examId = Integer.parseInt(examIdStr);
            int qid = Integer.parseInt(questionIdStr);

            boolean ok = complaintManager.submitComplaint(studentId, examId, qid, message.trim());
            if (ok) {
                resp.sendRedirect("complaint.html?status=ok");
            } else {
                resp.sendRedirect("complaint.html?status=error");
            }

        } catch (NumberFormatException e) {
            resp.sendRedirect("complaint.html?status=error");
        }
    }
}
