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
 * StudentServlet - Handles all REST API endpoints for Student operations.
 * 
 * Supported endpoints:
 * GET /students - List all students
 * POST /students - Add new student
 * GET /students/{id} - Get student by ID
 * PUT /students/{id} - Update student
 * DELETE /students/{id} - Delete student
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
     * GET /students - List all students
     * GET /students/{id} - Get student by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set the response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the path information from the request URL
        String pathInfo = request.getPathInfo();
        // Get a writer to send response data back to the client
        PrintWriter out = response.getWriter();

        // Start error handling block
        try {
            // Check if path does not include any ID
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /students - List all students

                // Get all students from the data store
                List<Student> students = dataStore.getAllStudents();
                // Convert the list of students to JSON format
                String jsonResponse = studentsToJson(students);

                // Set HTTP status to 200 OK
                response.setStatus(HttpServletResponse.SC_OK);
                // Send the JSON response to the client
                out.print(jsonResponse);

                // Log the successful operation to console
                System.out.println("GET /students - Returned " + students.size() + " students");

            } else {
                // GET /students/{id} - Get student by ID

                // Split the path by "/" to extract the ID
                String[] pathParts = pathInfo.split("/");
                // Check if we have exactly 2 parts (empty string and ID)
                if (pathParts.length == 2) {
                    // Number parsing error handling
                    try {
                        // Convert the second part to an integer (student ID)
                        int studentId = Integer.parseInt(pathParts[1]);
                        // Look up the student by ID in the data store
                        Student student = dataStore.getStudentById(studentId);

                        // Check if student was found
                        if (student != null) {
                            // Convert the single student to JSON format
                            String jsonResponse = studentToJson(student);
                            // Set HTTP status to 200 OK
                            response.setStatus(HttpServletResponse.SC_OK);
                            // Send the JSON response to the client
                            out.print(jsonResponse);

                            // Log the successful student retrieval
                            System.out.println("GET /students/" + studentId + " - Student found");
                        } else {
                            // Set HTTP status to 404 Not Found
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            // Send error message as JSON
                            out.print("{\"error\":\"Student not found with ID: " + studentId + "\"}");

                            // Log that student was not found
                            System.out.println("GET /students/" + studentId + " - Student not found");
                        }
                        // Handle case where ID is not a valid number
                    } catch (NumberFormatException e) {
                        // Set HTTP status to 400 Bad Request
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        // Send error message for invalid ID format
                        out.print("{\"error\":\"Invalid student ID format\"}");

                        // Log the invalid ID format error
                        System.out.println("GET /students - Invalid ID format: " + pathParts[1]);
                    }
                } else {
                    // Set HTTP status to 400 Bad Request for wrong URL format
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    // Send error message for invalid URL structure
                    out.print("{\"error\":\"Invalid URL format\"}");
                }
            }
            // Handle any unexpected errors
        } catch (Exception e) {
            // Set HTTP status to 500 Internal Server Error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            // Send error message with exception details
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            // Print the full stack trace to server logs
            e.printStackTrace();
            // Always execute this block regardless of success or failure
        } finally {
            // Ensure all buffered output is sent to the client
            out.flush();
        }
    }

    /**
     * POST /students - Add new student
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        // Get a writer to send response data back to the client
        PrintWriter out = response.getWriter();
        try {
            // Read JSON from request body
            // Create a buffer to collect all lines from the request JSON
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            // Get a reader to read the request body
            BufferedReader reader = request.getReader();
            // Read each line from the request body until there are no more lines
            while ((line = reader.readLine()) != null) {
                // Add each line to buffer
                jsonBuffer.append(line);
            }
            // Convert the buffer to a single string containing all JSON data
            String jsonInput = jsonBuffer.toString();
            // Check if the request body is empty after removing whitespace
            if (jsonInput.trim().isEmpty()) {
                // Set HTTP status to 400 Bad Request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // Send error message for empty request body
                out.print("{\"error\":\"Request body is empty\"}");
                // Exit the method early
                return;
            }
            // Parse JSON to Student object
            // Convert the JSON string into a Student object
            Student newStudent = parseStudentFromJson(jsonInput);
            // Check if parsing failed (returned null)
            if (newStudent == null) {
                // Set HTTP status to 400 Bad Request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // Send error message for invalid JSON
                out.print("{\"error\":\"Invalid JSON format\"}");
                // Exit the method early
                return;
            }
            // Validate required fields
            // Check if full name is missing or empty after trimming whitespace
            if (newStudent.getFullName() == null || newStudent.getFullName().trim().isEmpty()) {
                // Set HTTP status to 400 Bad Request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // Send error message for missing full name
                out.print("{\"error\":\"Full name is required\"}");
                // Exit the method early
                return;
            }
            // Check if email is missing or empty after trimming whitespace
            if (newStudent.getEmail() == null || newStudent.getEmail().trim().isEmpty()) {
                // Set HTTP status to 400 Bad Request
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                // Send error message for missing email
                out.print("{\"error\":\"Email is required\"}");
                // Exit the method early
                return;
            }
            // Add student to datastore (ID will be auto-generated)
            // Save the new student to the data store and get back the saved version with ID
            Student savedStudent = dataStore.addStudent(newStudent);
            // Convert the saved student to JSON format
            String jsonResponse = studentToJson(savedStudent);
            // Set HTTP status to 201 Created
            response.setStatus(HttpServletResponse.SC_CREATED);
            // Send the JSON response with the created student data
            out.print(jsonResponse);
            // Log the successful student creation
            System.out.println("POST /students - Created student with ID: " + savedStudent.getId());
            // Handle any errors that occur during processing
        } catch (Exception e) {
            // Set HTTP status to 400 Bad Request
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            // Send error message with exception details
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
            // Log the error to console
            System.out.println("POST /students - Error: " + e.getMessage());
            // Always execute this block regardless of success or failure
        } finally {
            // Ensure all buffered output is sent to the client
            out.flush();
        }
    }

    /**
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
            if (json.startsWith("{"))
                json = json.substring(1);
            if (json.endsWith("}"))
                json = json.substring(0, json.length() - 1);

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
        if (str == null)
            return "";
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
        if (str == null)
            return "";
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