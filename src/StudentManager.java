import java.sql.*;
import java.util.Scanner;

public class StudentManager {

    public void registerStudent() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        boolean ok = registerStudent(id, name, pass);
        if (ok) {
            System.out.println("Registration successful");
        } else {
            System.out.println("Invalid ID or already registered");
        }
    }

    // Web-friendly registration method used by servlets
    // Behavior (simplified):
    //  - If a student row with this ID exists -> UPDATE name/password and set registered=true.
    //  - If no row exists         -> INSERT a new student row.
    public boolean registerStudent(int id, String name, String pass) {
        if (name == null || name.trim().isEmpty() || pass == null || pass.isEmpty()) {
            return false;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                return false;
            }

            PreparedStatement check =
                con.prepareStatement("SELECT * FROM student WHERE student_id=?");

            check.setInt(1, id);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement update = con.prepareStatement(
                    "UPDATE student SET name=?, password=?, registered=true WHERE student_id=?"
                );
                update.setString(1, name);
                update.setString(2, pass);
                update.setInt(3, id);
                update.executeUpdate();
                return true;
            } else {
                PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO student (student_id, name, password, registered, instructor_id) " +
                    "VALUES (?, ?, ?, true, NULL)"
                );
                insert.setInt(1, id);
                insert.setString(2, name);
                insert.setString(3, pass);
                insert.executeUpdate();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginStudent(int id, String pass) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps =
                con.prepareStatement("SELECT * FROM student WHERE student_id=? AND password=?");

            ps.setInt(1, id);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
