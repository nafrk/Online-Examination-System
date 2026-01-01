import java.sql.*;
import java.util.Scanner;

public class QuestionManager {

    public void addQuestion() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter exam ID: ");
        int examId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter question: ");
        String q = sc.nextLine();

        System.out.print("Correct answer: ");
        String ans = sc.nextLine();

        System.out.print("Explanation: ");
        String exp = sc.nextLine();

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "INSERT INTO question (exam_id, question, correct_answer, explanation) VALUES (?,?,?,?)"
                );

            ps.setInt(1, examId);
            ps.setString(2, q);
            ps.setString(3, ans);
            ps.setString(4, exp);
            ps.executeUpdate();

            System.out.println("Question added");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
