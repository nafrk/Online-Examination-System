import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ================= MANAGERS =================
        StudentManager studentManager = new StudentManager();
        InstructorManager instructorManager = new InstructorManager();
        AdminManager adminManager = new AdminManager();
        ExamManager examManager = new ExamManager();
        QuestionManager questionManager = new QuestionManager();
        ExamTakingManager examTakingManager = new ExamTakingManager();
        ComplaintManager complaintManager = new ComplaintManager();
        ResultReviewManager resultReviewManager = new ResultReviewManager();
        PerformanceAnalyticsManager analyticsManager = new PerformanceAnalyticsManager();

        boolean systemRunning = true;

        while (systemRunning) {

            System.out.println("\n=================================");
            System.out.println("        ONLINE EXAM SYSTEM");
            System.out.println("=================================");
            System.out.println("1. Student");
            System.out.println("2. Instructor");
            System.out.println("3. Admin");
            System.out.println("4. Exit");
            System.out.print("Choose user type: ");

            int userType = sc.nextInt();
            sc.nextLine();

            switch (userType) {

                // ================= STUDENT =================
                case 1 -> {
                    System.out.println("\n1. Register");
                    System.out.println("2. Login");
                    System.out.print("Choice: ");
                    int sChoice = sc.nextInt();
                    sc.nextLine();

                    if (sChoice == 1) {
                        studentManager.registerStudent();
                    } else if (sChoice == 2) {
                        System.out.print("Student ID: ");
                        int sid = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Password: ");
                        String spass = sc.nextLine();

                        if (studentManager.loginStudent(sid, spass)) {
                            System.out.println("Login successful!");

                            boolean studentMenu = true;
                            while (studentMenu) {

                                System.out.println("\n--- Student Menu ---");
                                System.out.println("1. Take Exam");
                                System.out.println("2. Submit Complaint");
                                System.out.println("3. Review Exam Result");
                                System.out.println("4. View Performance Analytics");
                                System.out.println("5. Logout");
                                System.out.print("Choice: ");

                                int sm = sc.nextInt();
                                sc.nextLine();

                                switch (sm) {
                                    case 1 -> {
                                        examManager.listExams();
                                        examTakingManager.takeExam(sid);
                                    }
                                    case 2 -> complaintManager.submitComplaint(sid);
                                    case 3 -> resultReviewManager.reviewResult(sid);
                                    case 4 -> analyticsManager.studentPerformance(sid);
                                    case 5 -> studentMenu = false;
                                    default -> System.out.println("Invalid choice");
                                }
                            }
                        } else {
                            System.out.println("Login failed");
                        }
                    }
                }

                // ================= INSTRUCTOR =================
                case 2 -> {
                    System.out.println("\n1. Register");
                    System.out.println("2. Login");
                    System.out.print("Choice: ");
                    int iChoice = sc.nextInt();
                    sc.nextLine();

                    if (iChoice == 1) {
                        instructorManager.registerInstructor();
                    } else if (iChoice == 2) {
                        System.out.print("Instructor ID: ");
                        int iid = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Password: ");
                        String ipass = sc.nextLine();

                        if (instructorManager.loginInstructor(iid, ipass)) {
                            instructorManager.setInstructorId(iid);
                            System.out.println("Login successful!");

                            boolean instructorMenu = true;
                            while (instructorMenu) {

                                System.out.println("\n--- Instructor Menu ---");
                                System.out.println("1. Create Exam");
                                System.out.println("2. Add Question");
                                System.out.println("3. Make Question Bonus");
                                System.out.println("4. Add Student");
                                System.out.println("5. Track Students & Performance");
                                System.out.println("6. View Instructor Performance");
                                System.out.println("7. Logout");
                                System.out.print("Choice: ");

                                int im = sc.nextInt();
                                sc.nextLine();

                                switch (im) {
                                    case 1 -> examManager.createExam();
                                    case 2 -> questionManager.addQuestion();
                                    case 3 -> {
                                        System.out.print("Enter Question ID: ");
                                        int qid = sc.nextInt();
                                        sc.nextLine();
                                        complaintManager.makeBonus(qid);
                                    }
                                    case 4 -> {
                                        System.out.print("Enter Student ID: ");
                                        int sid = sc.nextInt();
                                        sc.nextLine();
                                        instructorManager.addStudent(sid);
                                    }
                                    case 5 -> {
                                        instructorManager.trackStudents();

                                        System.out.println("\nDo you want to view performance of these students? (yes/no)");
                                        String choice = sc.nextLine();
                                        if (choice.equalsIgnoreCase("yes")) {
                                            try {
                                                Connection con = DBConnection.getConnection();
                                                PreparedStatement ps = con.prepareStatement(
                                                        "SELECT student_id, name FROM student WHERE instructor_id=?"
                                                );
                                                ps.setInt(1, iid);
                                                ResultSet rs = ps.executeQuery();

                                                System.out.println("\n--- Students Performance ---");
                                                System.out.printf("%-10s %-20s %-12s %-12s %-12s%n",
                                                        "ID", "Name", "Total Exams", "Avg Score", "Max Score");
                                                System.out.println("--------------------------------------------------------------");

                                                while (rs.next()) {
                                                    int studentId = rs.getInt("student_id");
                                                    String name = rs.getString("name");

                                                    PreparedStatement perf = con.prepareStatement(
                                                            "SELECT COUNT(*) AS total_exams, AVG(score) AS avg_score, MAX(score) AS max_score " +
                                                                    "FROM result WHERE student_id=?"
                                                    );
                                                    perf.setInt(1, studentId);
                                                    ResultSet prs = perf.executeQuery();
                                                    if (prs.next()) {
                                                        System.out.printf("%-10d %-20s %-12d %-12.2f %-12d%n",
                                                                studentId,
                                                                name,
                                                                prs.getInt("total_exams"),
                                                                prs.getDouble("avg_score"),
                                                                prs.getInt("max_score"));
                                                    }
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    case 6 -> analyticsManager.instructorPerformance(iid);
                                    case 7 -> instructorMenu = false;
                                    default -> System.out.println("Invalid choice");
                                }
                            }
                        } else {
                            System.out.println("Login failed");
                        }
                    }
                }

                // ================= ADMIN =================
                case 3 -> {
                    System.out.print("Admin ID: ");
                    int aid = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Password: ");
                    String apass = sc.nextLine();

                    if (adminManager.loginAdmin(aid, apass)) {
                        System.out.println("Admin login successful");

                        boolean adminMenu = true;
                        while (adminMenu) {

                            System.out.println("\n--- Admin Menu ---");
                            System.out.println("1. Create Exam");
                            System.out.println("2. Add Question");
                            System.out.println("3. Add Instructor");
                            System.out.println("4. View All Users");
                            System.out.println("5. View Performance Analytics");
                            System.out.println("6. Logout");
                            System.out.print("Choice: ");

                            int am = sc.nextInt();
                            sc.nextLine();

                            switch (am) {
                                case 1 -> examManager.createExam();
                                case 2 -> questionManager.addQuestion();
                                case 3 -> {
                                    System.out.print("Enter new Instructor ID: ");
                                    int newId = sc.nextInt();
                                    sc.nextLine();
                                    adminManager.addInstructor(newId);
                                }
                                case 4 -> adminManager.viewUsers();
                                case 5 -> {
                                    System.out.println("\n1. Individual Student Performance");
                                    System.out.println("2. Class Performance");
                                    System.out.println("3. Instructor Performance");
                                    System.out.println("4. All Students under an Instructor");
                                    System.out.print("Choice: ");
                                    int pChoice = sc.nextInt();
                                    sc.nextLine();

                                    switch (pChoice) {
                                        case 1 -> {
                                            System.out.print("Enter Student ID: ");
                                            int sid = sc.nextInt();
                                            sc.nextLine();
                                            analyticsManager.studentPerformance(sid);
                                        }
                                        case 2 -> {
                                            System.out.print("Enter Exam ID: ");
                                            int examId = sc.nextInt();
                                            sc.nextLine();
                                            analyticsManager.classPerformance(examId);
                                        }
                                        case 3 -> {
                                            System.out.print("Enter Instructor ID: ");
                                            int iid = sc.nextInt();
                                            sc.nextLine();
                                            analyticsManager.instructorPerformance(iid);
                                        }
                                        case 4 -> {
                                            System.out.print("Enter Instructor ID: ");
                                            int iid = sc.nextInt();
                                            sc.nextLine();

                                            try {
                                                Connection con = DBConnection.getConnection();
                                                PreparedStatement ps = con.prepareStatement(
                                                        "SELECT student_id, name FROM student WHERE instructor_id=?"
                                                );
                                                ps.setInt(1, iid);
                                                ResultSet rs = ps.executeQuery();

                                                System.out.println("\n--- Students Performance for Instructor ID " + iid + " ---");
                                                System.out.printf("%-10s %-20s %-12s %-12s %-12s%n",
                                                        "ID", "Name", "Total Exams", "Avg Score", "Max Score");
                                                System.out.println("--------------------------------------------------------------");

                                                while (rs.next()) {
                                                    int studentId = rs.getInt("student_id");
                                                    String name = rs.getString("name");

                                                    PreparedStatement perf = con.prepareStatement(
                                                            "SELECT COUNT(*) AS total_exams, AVG(score) AS avg_score, MAX(score) AS max_score " +
                                                                    "FROM result WHERE student_id=?"
                                                    );
                                                    perf.setInt(1, studentId);
                                                    ResultSet prs = perf.executeQuery();
                                                    if (prs.next()) {
                                                        System.out.printf("%-10d %-20s %-12d %-12.2f %-12d%n",
                                                                studentId,
                                                                name,
                                                                prs.getInt("total_exams"),
                                                                prs.getDouble("avg_score"),
                                                                prs.getInt("max_score"));
                                                    }
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        default -> System.out.println("Invalid choice");
                                    }
                                }
                                case 6 -> adminMenu = false;
                                default -> System.out.println("Invalid choice");
                            }
                        }
                    } else {
                        System.out.println("Admin login failed");
                    }
                }

                // ================= EXIT =================
                case 4 -> {
                    systemRunning = false;
                    System.out.println("System closed.");
                }

                default -> System.out.println("Invalid user type");
            }
        }

        sc.close();
    }
}
