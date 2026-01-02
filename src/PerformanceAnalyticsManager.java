import java.sql.*;

/*
 * This class handles performance analytics for:
 *  - Students
 *  - Classes
 *  - Instructors
 */

public class PerformanceAnalyticsManager {

    // ================= STUDENT PERFORMANCE =================
    public void studentPerformance(int studentId) {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "SELECT COUNT(*) AS total_exams, " +
                    "AVG(score) AS avg_score, " +
                    "MAX(score) AS max_score " +
                    "FROM result WHERE student_id=?"
                );

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Student Performance ---");
                System.out.println("Total Exams Taken: " + rs.getInt("total_exams"));
                System.out.println("Average Score: " + rs.getDouble("avg_score"));
                System.out.println("Highest Score: " + rs.getInt("max_score"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CLASS PERFORMANCE =================
    public void classPerformance(int examId) {

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps =
                con.prepareStatement(
                    "SELECT COUNT(*) AS students, AVG(score) AS avg_score " +
                    "FROM result WHERE exam_id=?"
                );

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Class Performance ---");
                System.out.println("Total Students: " + rs.getInt("students"));
                System.out.println("Average Class Score: " + rs.getDouble("avg_score"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= INSTRUCTOR PERFORMANCE =================
    public void instructorPerformance(int instructorId) {

        try {
            Connection con = DBConnection.getConnection();

            // Exams created (schema does not track exams per instructor, so count all exams)
            PreparedStatement exams =
                con.prepareStatement(
                    "SELECT COUNT(*) FROM exam"
                );
            ResultSet rs1 = exams.executeQuery();
            rs1.next();

            // Complaints responded (bonus questions)
            PreparedStatement bonus =
                con.prepareStatement(
                    "SELECT COUNT(*) FROM question WHERE bonus=TRUE"
                );
            ResultSet rs2 = bonus.executeQuery();
            rs2.next();

            System.out.println("\n--- Instructor Performance ---");
            System.out.println("Exams Created: " + rs1.getInt(1));
            System.out.println("Bonus Questions Approved: " + rs2.getInt(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
