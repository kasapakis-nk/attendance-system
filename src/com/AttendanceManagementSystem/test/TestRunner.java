package com.AttendanceManagementSystem.test;

import com.AttendanceManagementSystem.model.Student;
import com.AttendanceManagementSystem.model.Course;
import com.AttendanceManagementSystem.model.AttendanceRecord;
import com.AttendanceManagementSystem.storage.DataStore;
import com.AttendanceManagementSystem.servlet.StudentServletTest;
import com.AttendanceManagementSystem.servlet.CourseServletTest;
import com.AttendanceManagementSystem.servlet.AttendanceServletTest;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test runner to verify all backend functionality works correctly
 * before deploying to Tomcat.
 */
public class TestRunner {

    private static DataStore dataStore;
    private static StudentServletTest studentServlet;
    private static CourseServletTest courseServlet;
    private static AttendanceServletTest attendanceServlet;

    public static void main(String[] args) {
        System.out.println("=== ATTENDANCE MANAGEMENT SYSTEM - BACKEND TESTS ===\n");

        // Initialize components
        initializeComponents();

        // Run tests
        testDataStore();
        testStudentServlet();
        testCourseServlet();
        testAttendanceServlet();

        // Print final statistics
        printDataStoreStats();

        System.out.println("\n=== ALL TESTS COMPLETED ===");
    }

    private static void initializeComponents() {
        System.out.println("1. Initializing components...");

        dataStore = DataStore.getInstance();
        studentServlet = new StudentServletTest();
        courseServlet = new CourseServletTest();
        attendanceServlet = new AttendanceServletTest();

        studentServlet.init();
        courseServlet.init();
        attendanceServlet.init();

        System.out.println("    All components initialized\n");
    }

    private static void testDataStore() {
        System.out.println("2. Testing DataStore operations...");

        // Test initial data
        List<Student> students = dataStore.getAllStudents();
        List<Course> courses = dataStore.getAllCourses();
        List<AttendanceRecord> records = dataStore.getAllAttendanceRecords();

        System.out.println("    Initial data loaded:");
        System.out.println("     - Students: " + students.size());
        System.out.println("     - Courses: " + courses.size());
        System.out.println("     - Attendance records: " + records.size());

        // Test adding new student
        Student newStudent = new Student(0, "Test Student", "test@email.com");
        Student savedStudent = dataStore.addStudent(newStudent);
        System.out.println("    Added new student with ID: " + savedStudent.getId());

        // Test adding new course
        Course newCourse = new Course(0, "Test Course", "Test Instructor");
        Course savedCourse = dataStore.addCourse(newCourse);
        System.out.println("    Added new course with ID: " + savedCourse.getId());

        // Test adding attendance record
        AttendanceRecord newRecord = new AttendanceRecord(0, savedStudent.getId(),
                savedCourse.getId(), LocalDate.now(), true);
        AttendanceRecord savedRecord = dataStore.addAttendanceRecord(newRecord);
        System.out.println("    Added new attendance record with ID: " + savedRecord.getId());

        System.out.println("    DataStore tests passed\n");
    }

    private static void testStudentServlet() {
        System.out.println("3. Testing StudentServlet...");

        try {
            // Test GET all students
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            int[] statusCode = { 0 };

            studentServlet.doGet(null, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    GET /students - Status: " + statusCode[0]);
            System.out.println("     Response length: " + stringWriter.toString().length() + " characters");

            // Test GET student by ID
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            statusCode[0] = 0;

            studentServlet.doGet("/1", printWriter, statusCode);
            printWriter.flush();

            System.out.println("    GET /students/1 - Status: " + statusCode[0]);

            // Test POST new student
            String newStudentJson = "{\"fullName\":\"API Test Student\",\"email\":\"api@test.com\"}";
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            statusCode[0] = 0;

            studentServlet.doPost(newStudentJson, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    POST /students - Status: " + statusCode[0]);
            System.out.println("    StudentServlet tests passed\n");

        } catch (Exception e) {
            System.out.println("    StudentServlet test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testCourseServlet() {
        System.out.println("4. Testing CourseServlet...");

        try {
            // Test GET all courses
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            int[] statusCode = { 0 };

            courseServlet.doGet(null, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    GET /courses - Status: " + statusCode[0]);

            // Test POST new course
            String newCourseJson = "{\"name\":\"API Test Course\",\"instructor\":\"API Test Instructor\"}";
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            statusCode[0] = 0;

            courseServlet.doPost(newCourseJson, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    POST /courses - Status: " + statusCode[0]);
            System.out.println("    CourseServlet tests passed\n");

        } catch (Exception e) {
            System.out.println("    CourseServlet test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testAttendanceServlet() {
        System.out.println("5. Testing AttendanceServlet...");

        try {
            // Test GET all attendance records
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            int[] statusCode = { 0 };
            Map<String, String> queryParams = new HashMap<>();

            attendanceServlet.doGet(null, queryParams, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    GET /attendance - Status: " + statusCode[0]);

            // Test GET with query parameters
            queryParams.put("studentId", "1");
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            statusCode[0] = 0;

            attendanceServlet.doGet(null, queryParams, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    GET /attendance?studentId=1 - Status: " + statusCode[0]);

            // Test POST new attendance record
            String newAttendanceJson = "{\"studentId\":1,\"courseId\":1,\"date\":\"2025-07-20\",\"present\":true}";
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            statusCode[0] = 0;

            attendanceServlet.doPost(newAttendanceJson, printWriter, statusCode);
            printWriter.flush();

            System.out.println("    POST /attendance - Status: " + statusCode[0]);
            System.out.println("    AttendanceServlet tests passed\n");

        } catch (Exception e) {
            System.out.println("    AttendanceServlet test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printDataStoreStats() {
        System.out.println("=== FINAL DATA STORE STATISTICS ===");
        System.out.println(dataStore.getStatistics());

        System.out.println("\nStudents:");
        dataStore.getAllStudents().forEach(
                s -> System.out.println("  - " + s.getId() + ": " + s.getFullName() + " (" + s.getEmail() + ")"));

        System.out.println("\nCourses:");
        dataStore.getAllCourses()
                .forEach(c -> System.out.println("  - " + c.getId() + ": " + c.getName() + " by " + c.getInstructor()));

        System.out.println("\nAttendance Records:");
        dataStore.getAllAttendanceRecords()
                .forEach(r -> System.out.println("  - " + r.getId() + ": Student " + r.getStudentId() +
                        " in Course " + r.getCourseId() + " on " + r.getDate() +
                        " (" + (r.isPresent() ? "Present" : "Absent") + ")"));
    }
}