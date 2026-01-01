import java.sql.*;
import java.util.Scanner;

/*
 * This class allows a student to review a completed exam.
 * The student can see:
 *  - Each question
 *  - Their answer
 *  - Correct answer
 *  - Explanation
 *  - Bonus status
 *
 * Review is allowed only if the exam is already completed.
 */

public class ResultReviewManager {

    public void reviewResult(int studentId) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Exam ID to review: ");
        int examId = sc.nextInt();

        try {
            Connection con = DBConnection.getConnection();

            // ================= STEP 1: CHECK IF RESULT EXISTS =================
            PreparedStatement check =
                con.prepareStatement(
                    "SELECT score FROM result WHERE student_id=? AND exam_id=?"
                );

            check.setInt(1, studentId);
            check.setInt(2, examId);

            ResultSet rsCheck = check.executeQuery();

            if (!rsCheck.next()) {
                System.out.println("You have not completed this exam yet.");
                return;
            }

            int score = rsCheck.getInt("score");
            System.out.println("\nFinal Score: " + score);

            // ================= STEP 2: FETCH QUESTIONS + ANSWERS =================
            PreparedStatement ps =
                con.prepareStatement(
                    "SELECT q.question, q.correct_answer, q.explanation, q.bonus, sa.student_answer " +
                    "FROM question q " +
                    "JOIN student_answer sa ON q.question_id = sa.question_id " +
                    "WHERE sa.student_id=? AND sa.exam_id=?"
                );

            ps.setInt(1, studentId);
            ps.setInt(2, examId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n========== EXAM REVIEW ==========");

            int qNo = 1;
            while (rs.next()) {

                System.out.println("\nQuestion " + qNo++);
                System.out.println("Q: " + rs.getString("question"));
                System.out.println("Your Answer: " + rs.getString("student_answer"));
                System.out.println("Correct Answer: " + rs.getString("correct_answer"));
                System.out.println("Explanation: " + rs.getString("explanation"));

                if (rs.getBoolean("bonus")) {
                    System.out.println("Status: BONUS QUESTION (full marks awarded)");
                }
            }

            System.out.println("\n========== END OF REVIEW ==========");

        } catch (Exception e) {
            System.out.println("Error while reviewing result");
            e.printStackTrace();
        }
    }
}
