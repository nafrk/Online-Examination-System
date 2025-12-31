import java.sql.*;

public class AdminManager {

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

    // Add instructor with ID 
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

}

