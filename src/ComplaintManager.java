import java.sql.*;
import java.util.Scanner;

public class ComplaintManager {

    public void submitComplaint(int studentId) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Exam ID: ");
        int examId = sc.nextInt();

        System.out.print("Question ID: ");
        int qid = sc.nextInt();
        sc.nextLine();

        System.out.print("Complaint: ");
        String msg = sc.nextLine();

        boolean ok = submitComplaint(studentId, examId, qid, msg);
        if (ok) {
            System.out.println("Complaint submitted");
        } else {
            System.out.println("Failed to submit complaint");
        }
    }

    // Web-friendly overload for servlets
    public boolean submitComplaint(int studentId, int examId, int qid, String msg) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "INSERT INTO complaint (student_id, exam_id, question_id, message, status) VALUES (?,?,?,?, 'OPEN')"
                );

            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, qid);
            ps.setString(4, msg);
            ps.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void makeBonus(int questionId) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "UPDATE question SET bonus=true WHERE question_id=?"
                );

            ps.setInt(1, questionId);
            ps.executeUpdate();

            System.out.println("Question marked as BONUS for all students");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
