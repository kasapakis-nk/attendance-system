package com.AttendanceManagementSystem.storage;

import com.AttendanceManagementSystem.model.AttendanceRecord;
import com.AttendanceManagementSystem.model.Course;
import com.AttendanceManagementSystem.model.Student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * In-memory data store for the Student Attendance Management System.
 * Provides thread-safe CRUD operations.
 */
public class DataStore {
    // Single instance across the application
    private static DataStore instance;

    // Thread-safe collections (handles multiple concurrent users)
    private final ConcurrentHashMap<Integer, Student> students;
    private final ConcurrentHashMap<Integer, Course> courses;
    private final ConcurrentHashMap<Integer, AttendanceRecord> attendanceRecords;

    // Thread-safe ID generators
    private final AtomicInteger studentIdCounter;
    private final AtomicInteger courseIdCounter;
    private final AtomicInteger attendanceIdCounter;

    /**
     * Private constructor for singleton pattern
     */
    private DataStore() {
        students = new ConcurrentHashMap<>();
        courses = new ConcurrentHashMap<>();
        attendanceRecords = new ConcurrentHashMap<>();

        studentIdCounter = new AtomicInteger(1);
        courseIdCounter = new AtomicInteger(1);
        attendanceIdCounter = new AtomicInteger(1);

        // Initialize with some mock data
        initializeSampleData();
    }

    /**
     * Get singleton instance of DataStore
     * 
     * @return DataStore instance
     */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Get all students
     * 
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    /**
     * Get student by ID
     * 
     * @param id Student ID
     * @return Student if found, null otherwise
     */
    public Student getStudentById(int id) {
        return students.get(id);
    }

    /**
     * Add new student
     * 
     * @param student Student to add (ID will be auto-generated)
     * @return Added student with generated ID
     */
    public Student addStudent(Student student) {
        int newId = studentIdCounter.getAndIncrement();
        student.setId(newId);
        students.put(newId, student);
        return student;
    }

    /**
     * Update existing student
     * 
     * @param student Student to update
     * @return Updated student if found, null otherwise
     */
    public Student updateStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    /**
     * Delete student by ID
     * 
     * @param id Student ID
     * @return true if deleted, false if not found
     */
    public boolean deleteStudent(int id) {
        return students.remove(id) != null;
    }

    /**
     * Get all courses
     * 
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    /**
     * Get course by ID
     * 
     * @param id Course ID
     * @return Course if found, null otherwise
     */
    public Course getCourseById(int id) {
        return courses.get(id);
    }

    /**
     * Add new course
     * 
     * @param course Course to add (ID will be auto-generated)
     * @return Added course with generated ID
     */
    public Course addCourse(Course course) {
        int newId = courseIdCounter.getAndIncrement();
        course.setId(newId);
        courses.put(newId, course);
        return course;
    }

    /**
     * Update existing course
     * 
     * @param course Course to update
     * @return Updated course if found, null otherwise
     */
    public Course updateCourse(Course course) {
        if (courses.containsKey(course.getId())) {
            courses.put(course.getId(), course);
            return course;
        }
        return null;
    }

    /**
     * Delete course by ID
     * 
     * @param id Course ID
     * @return true if deleted, false if not found
     */
    public boolean deleteCourse(int id) {
        return courses.remove(id) != null;
    }

    /**
     * Get all attendance records
     * 
     * @return List of all attendance records
     */
    public List<AttendanceRecord> getAllAttendanceRecords() {
        return new ArrayList<>(attendanceRecords.values());
    }

    /**
     * Get attendance record by ID
     * 
     * @param id Attendance record ID
     * @return AttendanceRecord if found, null otherwise
     */
    public AttendanceRecord getAttendanceRecordById(int id) {
        return attendanceRecords.get(id);
    }

    /**
     * Get attendance records by student and course
     * 
     * @param studentId Student ID (optional, use -1 to ignore)
     * @param courseId  Course ID (optional, use -1 to ignore)
     * @return List of matching attendance records
     */
    public List<AttendanceRecord> getAttendanceRecords(int studentId, int courseId) {
        return attendanceRecords.values().stream()
                .filter(record -> (studentId == -1 || record.getStudentId() == studentId))
                .filter(record -> (courseId == -1 || record.getCourseId() == courseId))
                .collect(Collectors.toList());
    }

    /**
     * Add new attendance record
     * 
     * @param record AttendanceRecord to add (ID will be auto-generated)
     * @return Added attendance record with generated ID
     */
    public AttendanceRecord addAttendanceRecord(AttendanceRecord record) {
        int newId = attendanceIdCounter.getAndIncrement();
        record.setId(newId);
        attendanceRecords.put(newId, record);
        return record;
    }

    /**
     * Update existing attendance record
     * 
     * @param record AttendanceRecord to update
     * @return Updated record if found, null otherwise
     */
    public AttendanceRecord updateAttendanceRecord(AttendanceRecord record) {
        if (attendanceRecords.containsKey(record.getId())) {
            attendanceRecords.put(record.getId(), record);
            return record;
        }
        return null;
    }

    /**
     * Delete attendance record by ID
     * 
     * @param id Attendance record ID
     * @return true if deleted, false if not found
     */
    public boolean deleteAttendanceRecord(int id) {
        return attendanceRecords.remove(id) != null;
    }
    
    /**
     * Check if student exists
     * 
     * @param studentId Student ID
     * @return true if student exists
     */
    public boolean studentExists(int studentId) {
        return students.containsKey(studentId);
    }

    /**
     * Check if course exists
     * 
     * @param courseId Course ID
     * @return true if course exists
     */
    public boolean courseExists(int courseId) {
        return courses.containsKey(courseId);
    }

    /**
     * Get statistics about the data store
     * 
     * @return String with current statistics
     */
    public String getStatistics() {
        return String.format("DataStore Statistics: %d students, %d courses, %d attendance records",
                students.size(), courses.size(), attendanceRecords.size());
    }

    /**
     * Initialize sample data for testing
     */
    private void initializeSampleData() {
        // Mock students
        addStudent(new Student(0, "Mitsos Karatasou", "mitsos.kara@gmail.com"));
        addStudent(new Student(0, "Maria Eleutheriou", "maria.ele@email.com"));
        addStudent(new Student(0, "Arnold Schwarz", "arnie.sch@email.com"));

        // Mock courses
        addCourse(new Course(0, "Java Programming", "Dr. John"));
        addCourse(new Course(0, "Data Structures", "Prof. Davis"));
        addCourse(new Course(0, "Web Development", "Sir Mathews"));

        // Mock attendance records
        addAttendanceRecord(new AttendanceRecord(0, 1, 1, LocalDate.now().minusDays(1), true));
        addAttendanceRecord(new AttendanceRecord(0, 1, 2, LocalDate.now().minusDays(1), false));
        addAttendanceRecord(new AttendanceRecord(0, 2, 1, LocalDate.now().minusDays(1), true));
        addAttendanceRecord(new AttendanceRecord(0, 3, 3, LocalDate.now(), true));
    }

    /**
     * Clear all data (useful for testing)
     */
    public void clearAllData() {
        students.clear();
        courses.clear();
        attendanceRecords.clear();
        studentIdCounter.set(1);
        courseIdCounter.set(1);
        attendanceIdCounter.set(1);
    }
}