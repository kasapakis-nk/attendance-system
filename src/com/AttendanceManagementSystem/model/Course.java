package com.AttendanceManagementSystem.model;

/**
 * Course entity representing a course in the attendance management system.
 * Contains course information including ID, name, and instructor.
 */
public class Course {
    private int id;
    private String name;
    private String instructor;

    /**
     * Default constructor
     */
    public Course() {
    }

    /**
     * Constructor with all fields
     * 
     * @param id         Course ID
     * @param name       Course name
     * @param instructor Instructor name
     */
    public Course(int id, String name, String instructor) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Course course = (Course) obj;

        return id == course.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}