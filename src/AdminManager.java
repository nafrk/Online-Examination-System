import java.sql.*;

public class AdminManager {

    // Admin login
    public boolean loginAdmin(int id, String pass) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM admin WHERE admin_id=? AND password=?"
            );
            ps.setInt(1, id);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add instructor with ID (pre-register)
    public void addInstructor(int id) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO instructor (instructor_id, approved) VALUES (?, true)"
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Instructor added with ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View all students and instructors
    public void viewUsers() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            System.out.println("--- Instructors ---");
            ResultSet rs1 = st.executeQuery("SELECT * FROM instructor");
            while(rs1.next()) {
                System.out.println(rs1.getInt("instructor_id") + " | Name: " + rs1.getString("name") +
                                   " | Approved: " + rs1.getBoolean("approved"));
            }

            System.out.println("--- Students ---");
            ResultSet rs2 = st.executeQuery("SELECT * FROM student");
            while(rs2.next()) {
                System.out.println(rs2.getInt("student_id") + " | Name: " + rs2.getString("name") +
                                   " | Registered: " + rs2.getBoolean("registered") +
                                   " | Instructor ID: " + rs2.getInt("instructor_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
