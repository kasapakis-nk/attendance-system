package com.AttendanceManagementSystem.servlet;

import com.AttendanceManagementSystem.model.Course;
import com.AttendanceManagementSystem.storage.DataStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * CourseServlet - Handles all REST API endpoints for Course operations.
 * 
 * Supported endpoints:
 * GET    /courses        - List all courses
 * POST   /courses        - Add new course
 * GET    /courses/{id}   - Get course by ID
 * PUT    /courses/{id}   - Update course
 * DELETE /courses/{id}   - Delete course
 */
public class CourseServlet extends HttpServlet {
    
    private DataStore dataStore;

    @Override
    public void init() throws ServletException {
        super.init();
        dataStore = DataStore.getInstance();
        System.out.println("CourseServlet initialized successfully for Tomcat");
    }

    /**
     * GET /courses - List all courses
     * GET /courses/{id} - Get course by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /courses - List all courses
                List<Course> courses = dataStore.getAllCourses();
                String jsonResponse = coursesToJson(courses);
                
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                
                System.out.println("GET /courses - Returned " + courses.size() + " courses");
                
            } else {
                // GET /courses/{id} - Get course by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        int courseId = Integer.parseInt(pathParts[1]);
                        Course course = dataStore.getCourseById(courseId);
                        
                        if (course != null) {
                            String jsonResponse = courseToJson(course);
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(jsonResponse);
                            
                            System.out.println("GET /courses/" + courseId + " - Course found");
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"error\":\"Course not found with ID: " + courseId + "\"}");
                            
                            System.out.println("GET /courses/" + courseId + " - Course not found");
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\":\"Invalid course ID format\"}");
                        
                        System.out.println("GET /courses - Invalid ID format: " + pathParts[1]);
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid URL format\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }

    /**
     * POST /courses - Add new course
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Read JSON from request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            
            String jsonInput = jsonBuffer.toString();
            
            if (jsonInput.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Request body is empty\"}");
                return;
            }
            
            // Parse JSON to Course object manually
            Course newCourse = parseCourseFromJson(jsonInput);
            
            if (newCourse == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }
            
            // Validate required fields
            if (newCourse.getName() == null || newCourse.getName().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course name is required\"}");
                return;
            }
            
            if (newCourse.getInstructor() == null || newCourse.getInstructor().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Instructor is required\"}");
                return;
            }
            
            // Add course to datastore (ID will be auto-generated)
            Course savedCourse = dataStore.addCourse(newCourse);
            
            // Return created course
            String jsonResponse = courseToJson(savedCourse);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(jsonResponse);
            
            System.out.println("POST /courses - Created course with ID: " + savedCourse.getId());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
            
            System.out.println("POST /courses - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * PUT /courses/{id} - Update course
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course ID is required for update\"}");
                return;
            }
            
            // Extract course ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }
            
            int courseId;
            try {
                courseId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid course ID format\"}");
                return;
            }
            
            // Check if course exists
            if (!dataStore.courseExists(courseId)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Course not found with ID: " + courseId + "\"}");
                return;
            }
            
            // Read JSON from request body
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
            
            String jsonInput = jsonBuffer.toString();
            
            if (jsonInput.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Request body is empty\"}");
                return;
            }
            
            // Parse JSON to Course object
            Course updatedCourse = parseCourseFromJson(jsonInput);
            
            if (updatedCourse == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }
            
            updatedCourse.setId(courseId); // Ensure ID matches URL
            
            // Validate required fields
            if (updatedCourse.getName() == null || updatedCourse.getName().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course name is required\"}");
                return;
            }
            
            if (updatedCourse.getInstructor() == null || updatedCourse.getInstructor().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Instructor is required\"}");
                return;
            }
            
            // Update course in datastore
            Course savedCourse = dataStore.updateCourse(updatedCourse);
            
            // Return updated course
            String jsonResponse = courseToJson(savedCourse);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);
            
            System.out.println("PUT /courses/" + courseId + " - Course updated successfully");
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
            
            System.out.println("PUT /courses - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * DELETE /courses/{id} - Delete course
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course ID is required for deletion\"}");
                return;
            }
            
            // Extract course ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }
            
            int courseId;
            try {
                courseId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid course ID format\"}");
                return;
            }
            
            // Delete course from datastore
            boolean deleted = dataStore.deleteCourse(courseId);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\":\"Course deleted successfully\"}");
                
                System.out.println("DELETE /courses/" + courseId + " - Course deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Course not found with ID: " + courseId + "\"}");
                
                System.out.println("DELETE /courses/" + courseId + " - Course not found");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }

    /**
     * Convert Course object to JSON string
     */
    private String courseToJson(Course course) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(course.getId()).append(",");
        json.append("\"name\":\"").append(escapeJson(course.getName())).append("\",");
        json.append("\"instructor\":\"").append(escapeJson(course.getInstructor())).append("\"");
        json.append("}");
        return json.toString();
    }

    /**
     * Convert list of Courses to JSON array string
     */
    private String coursesToJson(List<Course> courses) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < courses.size(); i++) {
            json.append(courseToJson(courses.get(i)));
            if (i < courses.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }

    /**
     * Parse JSON string to Course object
     */
    private Course parseCourseFromJson(String json) {
        try {
            Course course = new Course();
            
            // Remove outer braces and whitespace
            json = json.trim();
            if (json.startsWith("{")) json = json.substring(1);
            if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
            
            // Split by commas (simple parsing - assumes well-formed JSON)
            String[] pairs = json.split(",");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    
                    switch (key) {
                        case "id":
                            if (!value.equals("null")) {
                                course.setId(Integer.parseInt(value));
                            }
                            break;
                        case "name":
                            course.setName(unescapeJson(value.replace("\"", "")));
                            break;
                        case "instructor":
                            course.setInstructor(unescapeJson(value.replace("\"", "")));
                            break;
                    }
                }
            }
            
            return course;
            
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Unescape JSON special characters
     */
    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }

    @Override
    public void destroy() {
        System.out.println("CourseServlet destroyed");
        super.destroy();
    }
}