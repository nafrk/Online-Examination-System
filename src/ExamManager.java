import java.sql.*;
import java.util.Scanner;

public class ExamManager {

    public void createExam() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter exam title: ");
        String title = sc.nextLine();

        System.out.print("Enter duration (minutes): ");
        int duration = sc.nextInt();

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "INSERT INTO exam (title, duration, status) VALUES (?, ?, 'ACTIVE')"
                );

            ps.setString(1, title);
            ps.setInt(2, duration);
            ps.executeUpdate();

            System.out.println("Exam created successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listExams() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT * FROM exam");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("exam_id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getInt("duration") + " min"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
