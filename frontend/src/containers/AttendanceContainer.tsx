import { useState, useEffect } from "react";
import AttendanceComp from "../components/AttendanceComp";
import type { AttendanceFormData } from "../types/types";
import type { Student } from "../entities/Student";
import type { Course } from "../entities/Course";
import type { AttendanceRecord } from "../entities/AttendanceRecord";

const API_BASE = "http://localhost:8080/attendance-system";

const AttendanceContainer = ({ showMessage }) => {
   const [attendanceRecords, setAttendanceRecords] = useState<AttendanceRecord[]>([]);
   const [studentsList, setStudentsList] = useState<Student[]>([]);
   const [coursesList, setCoursesList] = useState<Course[]>([]);
   const [loading, setLoading] = useState(false);
   const [operation, setOperation] = useState("create");
   const [formData, setFormData] = useState<AttendanceFormData>({
      id: "",
      studentId: "",
      courseId: "",
      date: "",
      present: true,
   });

   const resetForm = () => {
      setFormData({
         id: "",
         studentId: "",
         courseId: "",
         date: "",
         present: true,
      });
      setOperation("create");
   };

   const fetchAttendance = async (studentId = "", courseId = "") => {
      setLoading(true);
      try {
         let url = `${API_BASE}/attendance/`;
         const params = new URLSearchParams();
         if (studentId) params.append("studentId", studentId);
         if (courseId) params.append("courseId", courseId);
         if (params.toString()) url += `?${params.toString()}`;

         const response = await fetch(url);
         if (response.ok) {
            const result = await response.json();
            setAttendanceRecords(result);
         }
      } catch (error) {
         showMessage(`Error fetching attendance: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const fetchStudents = async () => {
      try {
         const response = await fetch(`${API_BASE}/students/`);
         if (response.ok) {
            const result = await response.json();
            setStudentsList(result);
         }
      } catch (error) {
         console.error("Error fetching students:", error);
      }
   };

   const fetchCourses = async () => {
      try {
         const response = await fetch(`${API_BASE}/courses/`);
         if (response.ok) {
            const result = await response.json();
            setCoursesList(result);
         }
      } catch (error) {
         console.error("Error fetching courses:", error);
      }
   };

   const handleSubmit = async () => {
      setLoading(true);

      try {
         let url = `${API_BASE}/attendance/`;
         let method = "POST";

         if (operation === "update") {
            url += formData.id;
            method = "PUT";
         }

         const body = {
            studentId: parseInt(formData.studentId),
            courseId: parseInt(formData.courseId),
            date: formData.date,
            present: formData.present,
         };

         const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
         });

         if (response.ok) {
            showMessage(
               `Attendance ${operation === "create" ? "recorded" : "updated"
               } successfully!`
            );
            resetForm();
            fetchAttendance();
         } else {
            const error = await response.json();
            showMessage(error.error || "Operation failed", "error");
         }
      } catch (error) {
         showMessage(`Error: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const handleDelete = async (id:number) => {
      if (!window.confirm("Delete this attendance record?")) return;

      try {
         const response = await fetch(`${API_BASE}/attendance/${id}`, {
            method: "DELETE",
         });
         if (response.ok) {
            showMessage("Attendance record deleted successfully!");
            fetchAttendance();
         } else {
            const error = await response.json();
            showMessage(error.error || "Delete failed", "error");
         }
      } catch (error) {
         showMessage(`Error: ${error.message}`, "error");
      }
   };

   const handleEdit = (record) => {
      setOperation("update");
      setFormData({
         id: record.id,
         studentId: record.studentId,
         courseId: record.courseId,
         date: record.date,
         present: record.present,
      });
   };

   useEffect(() => {
      fetchAttendance();
      fetchStudents();
      fetchCourses();
   }, []);

   return <AttendanceComp
      operation={operation}
      formData={formData}
      studentsList={studentsList}
      coursesList={coursesList}
      attendanceRecords={attendanceRecords}
      onFormChange={setFormData}
      resetForm={resetForm}
      loading={loading}
      fetchAttendance={fetchAttendance}
      handleEdit={handleEdit}
      handleDelete={handleDelete}
      handleSubmit={handleSubmit}
   />;
};

export default AttendanceContainer;
