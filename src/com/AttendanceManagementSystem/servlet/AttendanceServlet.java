package com.AttendanceManagementSystem.servlet;

import com.AttendanceManagementSystem.model.AttendanceRecord;
import com.AttendanceManagementSystem.storage.DataStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * AttendanceServlet - Handles all REST API endpoints for AttendanceRecord
 * operations
 * 
 * Supported endpoints:
 * GET /attendance - List all attendance records
 * GET /attendance?studentId=1 - Get attendance for specific student
 * GET /attendance?courseId=2 - Get attendance for specific course
 * GET /attendance?studentId=1&courseId=2 - Get attendance for student in course
 * POST /attendance - Add new attendance record
 * GET /attendance/{id} - Get attendance record by ID
 * PUT /attendance/{id} - Update attendance record
 * DELETE /attendance/{id} - Delete attendance record
 */
public class AttendanceServlet extends HttpServlet {

    private DataStore dataStore;

    @Override
    public void init() throws ServletException {
        super.init();
        dataStore = DataStore.getInstance();
        System.out.println("AttendanceServlet initialized successfully for Tomcat");
    }

    /**
     * Handle GET requests
     * Supports query parameters: studentId, courseId
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set the response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the path information from the request URL
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /attendance with optional query parameters

                // Call method to handle getting all attendance records
                getAllAttendanceRecords(request, response, out);

            } else {
                // GET /attendance/{id} - Get attendance record by ID

                // Call method to handle getting a specific attendance record by ID
                getAttendanceRecordById(pathInfo, response, out);
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
     * Handle POST requests
     * POST /attendance - Add new attendance record
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

            // Parse JSON to AttendanceRecord object manually
            AttendanceRecord newRecord = parseAttendanceRecordFromJson(jsonInput);

            if (newRecord == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }

            // Validate required fields
            if (newRecord.getStudentId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Valid student ID is required\"}");
                return;
            }

            if (newRecord.getCourseId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Valid course ID is required\"}");
                return;
            }

            if (newRecord.getDate() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Date is required\"}");
                return;
            }

            // Validate that student and course exist
            if (!dataStore.studentExists(newRecord.getStudentId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Student not found with ID: " + newRecord.getStudentId() + "\"}");
                return;
            }

            if (!dataStore.courseExists(newRecord.getCourseId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course not found with ID: " + newRecord.getCourseId() + "\"}");
                return;
            }

            // Add attendance record to datastore (ID will be auto-generated)
            AttendanceRecord savedRecord = dataStore.addAttendanceRecord(newRecord);

            // Return created attendance record
            String jsonResponse = attendanceRecordToJson(savedRecord);
            response.setStatus(HttpServletResponse.SC_CREATED);
            out.print(jsonResponse);

            System.out.println("POST /attendance - Created attendance record with ID: " + savedRecord.getId());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");

            System.out.println("POST /attendance - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * PUT /attendance/{id} - Update attendance record
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
                out.print("{\"error\":\"Attendance record ID is required for update\"}");
                return;
            }

            // Extract attendance record ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }

            int recordId;
            try {
                recordId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid attendance record ID format\"}");
                return;
            }

            // Check if attendance record exists
            if (dataStore.getAttendanceRecordById(recordId) == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Attendance record not found with ID: " + recordId + "\"}");
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

            // Parse JSON to AttendanceRecord object
            AttendanceRecord updatedRecord = parseAttendanceRecordFromJson(jsonInput);

            if (updatedRecord == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid JSON format\"}");
                return;
            }

            updatedRecord.setId(recordId); // Ensure ID matches URL

            // Validate required fields
            if (updatedRecord.getStudentId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Valid student ID is required\"}");
                return;
            }

            if (updatedRecord.getCourseId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Valid course ID is required\"}");
                return;
            }

            if (updatedRecord.getDate() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Date is required\"}");
                return;
            }

            // Validate that student and course exist
            if (!dataStore.studentExists(updatedRecord.getStudentId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Student not found with ID: " + updatedRecord.getStudentId() + "\"}");
                return;
            }

            if (!dataStore.courseExists(updatedRecord.getCourseId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course not found with ID: " + updatedRecord.getCourseId() + "\"}");
                return;
            }

            // Update attendance record in datastore
            AttendanceRecord savedRecord = dataStore.updateAttendanceRecord(updatedRecord);

            // Return updated attendance record
            String jsonResponse = attendanceRecordToJson(savedRecord);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);

            System.out.println("PUT /attendance/" + recordId + " - Attendance record updated successfully");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");

            System.out.println("PUT /attendance - Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    /**
     * DELETE /attendance/{id} - Delete attendance record
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
                out.print("{\"error\":\"Attendance record ID is required for deletion\"}");
                return;
            }

            // Extract attendance record ID from URL
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
                return;
            }

            int recordId;
            try {
                recordId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid attendance record ID format\"}");
                return;
            }

            // Delete attendance record from datastore
            boolean deleted = dataStore.deleteAttendanceRecord(recordId);

            if (deleted) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\":\"Attendance record deleted successfully\"}");

                System.out.println("DELETE /attendance/" + recordId + " - Attendance record deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Attendance record not found with ID: " + recordId + "\"}");

                System.out.println("DELETE /attendance/" + recordId + " - Attendance record not found");
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
     * Get all attendance records with optional filtering
     */
    private void getAllAttendanceRecords(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            int studentId = -1;
            int courseId = -1;

            // Parse query parameters
            String studentIdParam = request.getParameter("studentId");
            String courseIdParam = request.getParameter("courseId");

            if (studentIdParam != null) {
                try {
                    studentId = Integer.parseInt(studentIdParam);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid studentId parameter format\"}");
                    return;
                }
            }

            if (courseIdParam != null) {
                try {
                    courseId = Integer.parseInt(courseIdParam);
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid courseId parameter format\"}");
                    return;
                }
            }

            // Validate that referenced entities exist
            if (studentId > 0 && !dataStore.studentExists(studentId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Student not found with ID: " + studentId + "\"}");
                return;
            }

            if (courseId > 0 && !dataStore.courseExists(courseId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Course not found with ID: " + courseId + "\"}");
                return;
            }

            // Get filtered attendance records
            List<AttendanceRecord> records = dataStore.getAttendanceRecords(studentId, courseId);
            String jsonResponse = attendanceRecordsToJson(records);

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);

            System.out.println("GET /attendance - Returned " + records.size() + " attendance records" +
                    (studentId > 0 ? " for student " + studentId : "") +
                    (courseId > 0 ? " for course " + courseId : ""));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Get attendance record by ID
     */
    private void getAttendanceRecordById(String pathInfo, HttpServletResponse response, PrintWriter out) {
        try {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                try {
                    int recordId = Integer.parseInt(pathParts[1]);
                    AttendanceRecord record = dataStore.getAttendanceRecordById(recordId);

                    if (record != null) {
                        String jsonResponse = attendanceRecordToJson(record);
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print(jsonResponse);

                        System.out.println("GET /attendance/" + recordId + " - Attendance record found");
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"error\":\"Attendance record not found with ID: " + recordId + "\"}");

                        System.out.println("GET /attendance/" + recordId + " - Attendance record not found");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Invalid attendance record ID format\"}");

                    System.out.println("GET /attendance - Invalid ID format: " + pathParts[1]);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Invalid URL format\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Convert AttendanceRecord object to JSON string
     */
    private String attendanceRecordToJson(AttendanceRecord record) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(record.getId()).append(",");
        json.append("\"studentId\":").append(record.getStudentId()).append(",");
        json.append("\"courseId\":").append(record.getCourseId()).append(",");
        json.append("\"date\":\"").append(record.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\",");
        json.append("\"present\":").append(record.isPresent());
        json.append("}");
        return json.toString();
    }

    /**
     * Convert list of AttendanceRecords to JSON array string
     */
    private String attendanceRecordsToJson(List<AttendanceRecord> records) {
        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < records.size(); i++) {
            json.append(attendanceRecordToJson(records.get(i)));
            if (i < records.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Parse JSON string to AttendanceRecord object
     */
    private AttendanceRecord parseAttendanceRecordFromJson(String json) {
        try {
            AttendanceRecord record = new AttendanceRecord();

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
                                record.setId(Integer.parseInt(value));
                            }
                            break;
                        case "studentId":
                            record.setStudentId(Integer.parseInt(value));
                            break;
                        case "courseId":
                            record.setCourseId(Integer.parseInt(value));
                            break;
                        case "date":
                            String dateStr = value.replace("\"", "");
                            try {
                                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                                record.setDate(date);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format: " + dateStr);
                                return null;
                            }
                            break;
                        case "present":
                            record.setPresent(Boolean.parseBoolean(value));
                            break;
                    }
                }
            }

            return record;

        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void destroy() {
        System.out.println("AttendanceServlet destroyed");
        super.destroy();
    }
}