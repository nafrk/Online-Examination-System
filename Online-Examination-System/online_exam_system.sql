CREATE DATABASE online_exam_system;
USE online_exam_system;

-- ================= ADMIN =================
CREATE TABLE admin (
    admin_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE
);

-- ================= INSTRUCTOR =================
CREATE TABLE instructor (
    instructor_id INT PRIMARY KEY,
    name VARCHAR(100),
    password VARCHAR(100),
    approved BOOLEAN DEFAULT TRUE
);

-- ================= STUDENT =================
CREATE TABLE student (
    student_id INT PRIMARY KEY,
    name VARCHAR(100),
    password VARCHAR(100),
    registered BOOLEAN DEFAULT FALSE,
    instructor_id INT,
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id)
);

-- ================= EXAM (FIXED HERE) =================
CREATE TABLE exam (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    instructor_id INT NOT NULL,
    FOREIGN KEY (instructor_id) REFERENCES instructor(instructor_id)
);

-- ================= QUESTION =================
CREATE TABLE question (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question TEXT NOT NULL,
    correct_answer VARCHAR(255) NOT NULL,
    explanation TEXT,
    bonus BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

-- ================= RESULT =================
CREATE TABLE result (
    student_id INT,
    exam_id INT,
    score INT,
    start_time DATETIME,
    end_time DATETIME,
    PRIMARY KEY (student_id, exam_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id)
);

-- ================= COMPLAINT =================
CREATE TABLE complaint (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    exam_id INT,
    question_id INT,
    message TEXT,
    status VARCHAR(20) DEFAULT 'OPEN',
    response TEXT,
    response_time DATETIME,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- ================= STUDENT ANSWER =================
CREATE TABLE student_answer (
    student_id INT,
    exam_id INT,
    question_id INT,
    student_answer VARCHAR(255),
    PRIMARY KEY (student_id, exam_id, question_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (exam_id) REFERENCES exam(exam_id),
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

-- ================= INDEXES =================
CREATE INDEX idx_exam_status ON exam(status);
CREATE INDEX idx_exam_instructor ON exam(instructor_id);
CREATE INDEX idx_student_result ON result(student_id);
CREATE INDEX idx_complaint_status ON complaint(status);


USE online_exam_system;

-- ================= ADMIN =================
INSERT INTO admin VALUES
(1, 'Super Admin', 'admin123', '0912345678');

-- ================= INSTRUCTORS =================
INSERT INTO instructor VALUES
(101, 'John Doe', 'pass101', TRUE),
(102, 'Jane Smith', 'pass102', TRUE);

-- ================= STUDENTS =================
INSERT INTO student VALUES
(1001, 'Alice Johnson', 'alice123', TRUE, 101),
(1002, 'Bob Williams', 'bob123', TRUE, 101),
(1003, 'Charlie Brown', 'charlie123', TRUE, 102);

-- ================= EXAMS (NOW WITH instructor_id) =================
INSERT INTO exam (title, duration, status, instructor_id) VALUES
('Java Basics Exam', 60, 'ACTIVE', 101),
('Database Fundamentals', 45, 'ACTIVE', 102);

-- ================= QUESTIONS =================
INSERT INTO question (exam_id, question, correct_answer, explanation, bonus) VALUES
(1, 'What is JVM?', 'Java Virtual Machine', 'JVM executes Java bytecode', FALSE),
(1, 'What is the size of int in Java?', '4', 'Integer is 4 bytes in Java', FALSE),
(2, 'What does SQL stand for?', 'Structured Query Language', 'SQL is used to manage databases', FALSE),
(2, 'What is a primary key?', 'Unique identifier', 'Each table should have a primary key', TRUE);
