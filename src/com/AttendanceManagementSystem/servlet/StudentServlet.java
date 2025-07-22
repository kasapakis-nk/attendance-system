package com.AttendanceManagementSystem.servlet;

import com.AttendanceManagementSystem.model.Student;
import com.AttendanceManagementSystem.storage.DataStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * StudentServlet - Real HttpServlet for Tomcat deployment
 * Handles all REST API endpoints for Student operations.
 * 
 * Supported endpoints:
 * GET    /students        - List all students
 * POST   /students        - Add new student
 * GET    /students/{id}   - Get student by ID
 * PUT    /students/{id}   - Update student
 * DELETE /students/{id}   - Delete student
 */
public class StudentServlet extends HttpServlet {
    
    private DataStore dataStore;

    @Override
    public void init() throws ServletException {
        super.init();
        dataStore = DataStore.getInstance();
        System.out.println("StudentServlet initialized successfully for Tomcat");
    }

    /**
     * Handle GET requests
     * GET /students - List all students
     * GET /students/{id} - Get student by ID
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
                // GET /students - List all students
                List<Student> students = dataStore.getAllStudents();
                String jsonResponse = studentsToJson(students);
                
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                
                System.out.println("GET /students - Returned " + students.size() + " students");
                
            } else {
                // GET /students/{id} - Get student by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        int studentId = Integer.parseInt(pathParts[1]);
                        Student student = dataStore.getStudentById(studentId);
                        
                        if (student != null) {
                            String jsonResponse = studentToJson(student);
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(jsonResponse);
                            
                            System.out.println("GET /students/" + studentId + " - Student found");
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.print("{\"error\":\"Student not found with ID: " + studentId + "\"}");
                            
                            System.out.println("GET /students/" + studentId + " - Student not found");
                        }
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\":\"Invalid student ID format\"}");
                        
                        System.out.println("GET /students - Invalid ID format: " + pathParts[1]);
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
     * Handle POST requests
     * POST /students - Add new student
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
            
            // Parse JSON to Student object
            Student newStudent = parseStudentFromJson(jsonInput);
            
            if (newStudent == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }
            
            // Validate required fields
            if (newStudent.getFullName() == null || newStudent.getFullName().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Full name is required\"}");
                return;
            }
            
            if (newStudent.getEmail() == null || newStudent.getEmail().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Email is required\"}");
                return;
            }
            
            // Add student to datastore (ID will be auto-generated)
            Student savedStudent = dataStore.addStudent(newStudent);
            
            // Return created student
            String jsonResponse = studentToJson(savedStudent);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(jsonResponse);
            
            System.out.println("POST /students - Created student with ID: " + savedStudent.getId());
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
            
            System.out.println("POST /students - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * Handle PUT requests
     * PUT /students/{id} - Update student
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
                out.print("{\"error\":\"Student ID is required for update\"}");
                return;
            }
            
            // Extract student ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }
            
            int studentId;
            try {
                studentId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid student ID format\"}");
                return;
            }
            
            // Check if student exists
            if (!dataStore.studentExists(studentId)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Student not found with ID: " + studentId + "\"}");
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
            
            // Parse JSON to Student object
            Student updatedStudent = parseStudentFromJson(jsonInput);
            
            if (updatedStudent == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }
            
            updatedStudent.setId(studentId); // Ensure ID matches URL
            
            // Validate required fields
            if (updatedStudent.getFullName() == null || updatedStudent.getFullName().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Full name is required\"}");
                return;
            }
            
            if (updatedStudent.getEmail() == null || updatedStudent.getEmail().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Email is required\"}");
                return;
            }
            
            // Update student in datastore
            Student savedStudent = dataStore.updateStudent(updatedStudent);
            
            // Return updated student
            String jsonResponse = studentToJson(savedStudent);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);
            
            System.out.println("PUT /students/" + studentId + " - Student updated successfully");
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
            
            System.out.println("PUT /students - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * Handle DELETE requests
     * DELETE /students/{id} - Delete student
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
                out.print("{\"error\":\"Student ID is required for deletion\"}");
                return;
            }
            
            // Extract student ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }
            
            int studentId;
            try {
                studentId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid student ID format\"}");
                return;
            }
            
            // Delete student from datastore
            boolean deleted = dataStore.deleteStudent(studentId);
            
            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\":\"Student deleted successfully\"}");
                
                System.out.println("DELETE /students/" + studentId + " - Student deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Student not found with ID: " + studentId + "\"}");
                
                System.out.println("DELETE /students/" + studentId + " - Student not found");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }

    // ==================== MANUAL JSON PROCESSING ====================

    /**
     * Convert Student object to JSON string
     */
    private String studentToJson(Student student) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(student.getId()).append(",");
        json.append("\"fullName\":\"").append(escapeJson(student.getFullName())).append("\",");
        json.append("\"email\":\"").append(escapeJson(student.getEmail())).append("\",");
        json.append("\"registeredCourses\":[");
        
        List<Integer> courses = student.getRegisteredCourses();
        for (int i = 0; i < courses.size(); i++) {
            json.append(courses.get(i));
            if (i < courses.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]}");
        return json.toString();
    }

    /**
     * Convert list of Students to JSON array string
     */
    private String studentsToJson(List<Student> students) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < students.size(); i++) {
            json.append(studentToJson(students.get(i)));
            if (i < students.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }

    /**
     * Parse JSON string to Student object
     */
    private Student parseStudentFromJson(String json) {
        try {
            Student student = new Student();
            
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
                                student.setId(Integer.parseInt(value));
                            }
                            break;
                        case "fullName":
                            student.setFullName(unescapeJson(value.replace("\"", "")));
                            break;
                        case "email":
                            student.setEmail(unescapeJson(value.replace("\"", "")));
                            break;
                        case "registeredCourses":
                            // Parse array of course IDs
                            if (value.startsWith("[") && value.endsWith("]")) {
                                String arrayContent = value.substring(1, value.length() - 1);
                                if (!arrayContent.trim().isEmpty()) {
                                    String[] courseIds = arrayContent.split(",");
                                    List<Integer> courses = new ArrayList<>();
                                    for (String courseId : courseIds) {
                                        courses.add(Integer.parseInt(courseId.trim()));
                                    }
                                    student.setRegisteredCourses(courses);
                                }
                            }
                            break;
                    }
                }
            }
            
            return student;
            
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
        System.out.println("StudentServlet destroyed");
        super.destroy();
    }
}