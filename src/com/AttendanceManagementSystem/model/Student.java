package com.AttendanceManagementSystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Student entity representing a student in the attendance management system.
 * Contains student information and their registered courses.
 */
public class Student {
    private int id;
    private String fullName;
    private String email;
    private List<Integer> registeredCourses;

    /**
     * Default constructor
     */
    public Student() {
        this.registeredCourses = new ArrayList<>();
    }

    /**
     * Constructor with all fields except registered courses
     * 
     * @param id       Student ID
     * @param fullName Student's full name
     * @param email    Student's email address
     */
    public Student(int id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registeredCourses = new ArrayList<>();
    }

    /**
     * Constructor with all fields
     * 
     * @param id                Student ID
     * @param fullName          Student's full name
     * @param email             Student's email address
     * @param registeredCourses List of course IDs the student is registered for
     */
    public Student(int id, String fullName, String email, List<Integer> registeredCourses) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.registeredCourses = registeredCourses != null ? new ArrayList<>(registeredCourses) : new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getRegisteredCourses() {
        return new ArrayList<>(registeredCourses); // Return copy to prevent external modification
    }

    public void setRegisteredCourses(List<Integer> registeredCourses) {
        this.registeredCourses = registeredCourses != null ? new ArrayList<>(registeredCourses) : new ArrayList<>();
    }

    /**
     * Add a course to the student's registered courses
     * 
     * @param courseId The ID of the course to add
     * @return true if the course was added, false if already registered
     */
    public boolean addCourse(int courseId) {
        if (!registeredCourses.contains(courseId)) {
            registeredCourses.add(courseId);
            return true;
        }
        return false;
    }

    /**
     * Remove a course from the student's registered courses
     * 
     * @param courseId The ID of the course to remove
     * @return true if the course was removed, false if not found
     */
    public boolean removeCourse(int courseId) {
        return registeredCourses.remove(Integer.valueOf(courseId));
    }

    /**
     * Check if student is registered for a specific course
     * 
     * @param courseId The ID of the course to check
     * @return true if registered, false otherwise
     */
    public boolean isRegisteredForCourse(int courseId) {
        return registeredCourses.contains(courseId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", registeredCourses=" + registeredCourses +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true; // same obj reference === same students
        if (obj == null || getClass() != obj.getClass())
            return false; // null or diff class === not same students
        Student student = (Student) obj;

        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}