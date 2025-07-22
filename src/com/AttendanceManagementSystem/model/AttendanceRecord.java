package com.AttendanceManagementSystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * AttendanceRecord entity representing an attendance record in the system.
 * Links a student to a course for a specific date with attendance status.
 */
public class AttendanceRecord {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDate date;
    private boolean present;

    /**
     * Default constructor
     */
    public AttendanceRecord() {
    }

    /**
     * Constructor with all fields
     * 
     * @param id        Attendance record ID
     * @param studentId ID of the student
     * @param courseId  ID of the course
     * @param date      Date of attendance
     * @param present   Whether student was present
     */
    public AttendanceRecord(int id, int studentId, int courseId, LocalDate date, boolean present) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.present = present;
    }

    /**
     * Constructor without ID, for creating new records
     * 
     * @param studentId ID of the student
     * @param courseId  ID of the course
     * @param date      Date of attendance
     * @param present   Whether student was present
     */
    public AttendanceRecord(int studentId, int courseId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.present = present;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    /**
     * Get date as string in yyyy-MM-dd format for JSON serialization
     * 
     * @return Date as string
     */
    public String getDateString() {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    /**
     * Set date from string in yyyy-MM-dd format for JSON deserialization
     * 
     * @param dateString Date as string
     */
    public void setDateString(String dateString) {
        this.date = dateString != null ? LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    /**
     * Check if this attendance record matches the given student and course
     * 
     * @param studentId The student ID to check
     * @param courseId  The course ID to check
     * @return true if both IDs match
     */
    public boolean matchesStudentAndCourse(int studentId, int courseId) {
        return this.studentId == studentId && this.courseId == courseId;
    }

    /**
     * Check if this attendance record is for the given date
     * 
     * @param date The date to check
     * @return true if dates match
     */
    public boolean isAttendanceForThisDate(LocalDate date) {
        return this.date != null && this.date.equals(date);
    }

    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", date=" + date +
                ", present=" + present +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AttendanceRecord that = (AttendanceRecord) obj;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
