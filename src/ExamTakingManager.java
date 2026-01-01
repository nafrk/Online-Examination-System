import java.sql.*;
import java.util.Scanner;

public class ExamTakingManager {

    public void takeExam(int studentId) {

        Scanner sc = new Scanner(System.in);

        try {
            Connection con = DBConnection.getConnection();

            System.out.print("Enter Exam ID: ");
            int examId = sc.nextInt();
            sc.nextLine();

            // Collect answers using console, then reuse web-friendly grader
            java.util.Map<Integer, String> answers = new java.util.HashMap<>();

            // Check if already taken
            PreparedStatement check =
                con.prepareStatement(
                    "SELECT * FROM result WHERE student_id=? AND exam_id=?"
                );
            check.setInt(1, studentId);
            check.setInt(2, examId);

            ResultSet checkRs = check.executeQuery();
            if (checkRs.next()) {
                System.out.println("You already took this exam.");
                return;
            }

            // Fetch questions for console mode
            PreparedStatement qps =
                con.prepareStatement(
                    "SELECT question_id, question FROM question WHERE exam_id=?"
                );
            qps.setInt(1, examId);

            ResultSet rs = qps.executeQuery();
            while (rs.next()) {
                int qid = rs.getInt("question_id");
                System.out.println("\nQuestion:");
                System.out.println(rs.getString("question"));
                System.out.print("Your Answer: ");
                String userAns = sc.nextLine();
                answers.put(qid, userAns);
            }

            int score = gradeExam(studentId, examId, answers);
            if (score >= 0) {
                System.out.println("\nExam Finished!");
                System.out.println("Your Score: " + score);
            } else {
                System.out.println("Failed to grade exam.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Web-friendly grading used by servlets
    public int gradeExam(int studentId, int examId, java.util.Map<Integer, String> answers) {
        try {
            Connection con = DBConnection.getConnection();

            // Check if already taken
            PreparedStatement check =
                con.prepareStatement(
                    "SELECT * FROM result WHERE student_id=? AND exam_id=?"
                );
            check.setInt(1, studentId);
            check.setInt(2, examId);
            ResultSet checkRs = check.executeQuery();
            if (checkRs.next()) {
                // already taken
                return -1;
            }

            // Validate exam exists (we don't depend on a specific status value)
            PreparedStatement examPs =
                con.prepareStatement(
                    "SELECT duration FROM exam WHERE exam_id=?"
                );
            examPs.setInt(1, examId);
            ResultSet examRs = examPs.executeQuery();
            if (!examRs.next()) {
                return -1;
            }

            Timestamp startTime = new Timestamp(System.currentTimeMillis());

            // Fetch questions
            PreparedStatement qps =
                con.prepareStatement(
                    "SELECT * FROM question WHERE exam_id=?"
                );
            qps.setInt(1, examId);

            ResultSet rs = qps.executeQuery();

            int score = 0;

            while (rs.next()) {
                int qid = rs.getInt("question_id");
                String userAns = answers.getOrDefault(qid, "");

                String correct = rs.getString("correct_answer");
                boolean bonus = rs.getBoolean("bonus");

                // Save student answer
                PreparedStatement saveAns =
                    con.prepareStatement(
                        "INSERT INTO student_answer VALUES (?,?,?,?)"
                    );
                saveAns.setInt(1, studentId);
                saveAns.setInt(2, examId);
                saveAns.setInt(3, qid);
                saveAns.setString(4, userAns);
                saveAns.executeUpdate();

                // Evaluation
                if (userAns != null && userAns.equalsIgnoreCase(correct)) {
                    score++;
                } else if (bonus) {
                    score++; // bonus applied
                }
            }

            Timestamp endTime = new Timestamp(System.currentTimeMillis());

            // Save result
            PreparedStatement saveResult =
                con.prepareStatement(
                    "INSERT INTO result VALUES (?,?,?,?,?)"
                );
            saveResult.setInt(1, studentId);
            saveResult.setInt(2, examId);
            saveResult.setInt(3, score);
            saveResult.setTimestamp(4, startTime);
            saveResult.setTimestamp(5, endTime);
            saveResult.executeUpdate();

            return score;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
