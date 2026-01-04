import java.sql.*;
import java.util.Scanner;

public class InstructorManager {

    private int instructorId; // store logged-in instructor

    public void setInstructorId(int id) {
        this.instructorId = id;
    }

    // Instructor registration (console)
    public void registerInstructor() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Instructor ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM instructor WHERE instructor_id=? AND approved=true"
            );
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getString("name") == null) {
                System.out.print("Enter name: ");
                String name = sc.nextLine();
                System.out.print("Enter password: ");
                String pass = sc.nextLine();

                PreparedStatement update = con.prepareStatement(
                    "UPDATE instructor SET name=?, password=? WHERE instructor_id=?"
                );
                update.setString(1, name);
                update.setString(2, pass);
                update.setInt(3, id);
                update.executeUpdate();

                System.out.println("Instructor registered successfully");
            } else {
                System.out.println("Invalid ID or already registered");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Web-friendly instructor registration
    // Behavior: if an instructor row with this ID exists -> UPDATE name/password/approved.
    //           if no row exists                      -> INSERT a new instructor row.
    public boolean registerInstructor(int id, String name, String pass) {
        if (name == null || name.trim().isEmpty() || pass == null || pass.isEmpty()) {
            return false;
        }

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                return false;
            }

            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM instructor WHERE instructor_id=?"
            );
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                // Update existing instructor (handles rows created by admin or console)
                PreparedStatement update = con.prepareStatement(
                    "UPDATE instructor SET name=?, password=?, approved=true WHERE instructor_id=?"
                );
                update.setString(1, name);
                update.setString(2, pass);
                update.setInt(3, id);
                update.executeUpdate();
                return true;
            } else {
                // Create new instructor row if it does not exist yet
                PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO instructor (instructor_id, name, password, approved) VALUES (?, ?, ?, true)"
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

    // Instructor login
    public boolean loginInstructor(int id, String pass) {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM instructor WHERE instructor_id=? AND password=?"
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

    // Add student assigned to this instructor
    public void addStudent(int studentId) {
        try {
            Scanner sc = new Scanner(System.in);
            Connection con = DBConnection.getConnection();

            System.out.print("Enter student name: ");
            String name = sc.nextLine();
            System.out.print("Enter password: ");
            String pass = sc.nextLine();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO student (student_id, name, password, registered, instructor_id) VALUES (?,?,?,?,?)"
            );
            ps.setInt(1, studentId);
            ps.setString(2, name);
            ps.setString(3, pass);
            ps.setBoolean(4, false); // not yet registered
            ps.setInt(5, this.instructorId);
            ps.executeUpdate();

            System.out.println("Student added with ID: " + studentId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Track students assigned to this instructor
    public void trackStudents() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM student WHERE instructor_id=?"
            );
            ps.setInt(1, this.instructorId);
            ResultSet rs = ps.executeQuery();

            System.out.println("--- Your Students ---");
            while (rs.next()) {
                System.out.println(rs.getInt("student_id") + " | Name: " + rs.getString("name") +
                                   " | Registered: " + rs.getBoolean("registered"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
