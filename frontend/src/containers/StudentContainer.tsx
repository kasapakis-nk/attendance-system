import { useState, useEffect } from "react";
import StudentComp from "../components/StudentComp";
import { Student } from "../entities/Student"

const API_BASE = "http://localhost:8080/attendance-system";

export interface StudentContainerProps {
   onShowMessage: (text: string, type?: string) => void
}

const StudentContainer = ({ onShowMessage }: StudentContainerProps) => {
   const [studentsList, setStudentsList] = useState<Student[]>([]);
   const [loading, setLoading] = useState(false);
   const [operation, setOperation] = useState("create");
   const [studentFormData, setStudentFormData] = useState({
      id: "",
      fullName: "",
      email: "",
   });

   const resetForm = () => {
      setStudentFormData({
         id: "",
         fullName: "",
         email: "",
      });
      setOperation("create");
   };

   const fetchStudents = async () => {
      setLoading(true);
      try {
         const response = await fetch(`${API_BASE}/students/`);
         if (response.ok) {
            const result = await response.json();
            setStudentsList(result);
         }
      } catch (error) {
         onShowMessage(`Error fetching students: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const handleSubmit = async () => {
      setLoading(true);

      try {
         let url = `${API_BASE}/students/`;
         let method = "POST";

         if (operation === "update") {
            url += studentFormData.id;
            method = "PUT";
         }

         const body = {
            fullName: studentFormData.fullName,
            email: studentFormData.email,
            registeredCourses: [],
         };

         const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
         });

         if (response.ok) {
            onShowMessage(
               `Student ${operation === "create" ? "created" : "updated"} successfully!`
            );
            resetForm();
            fetchStudents();
         } else {
            const error = await response.json();
            onShowMessage(error.error || "Operation failed", "error");
         }
      } catch (error) {
         onShowMessage(`Error: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const handleDelete = async (id) => {
      if (!window.confirm("Delete this student?")) return;

      try {
         const response = await fetch(`${API_BASE}/students/${id}`, {
            method: "DELETE",
         });
         if (response.ok) {
            onShowMessage("Student deleted successfully!");
            fetchStudents();
         } else {
            const error = await response.json();
            onShowMessage(error.error || "Delete failed", "error");
         }
      } catch (error) {
         onShowMessage(`Error: ${error.message}`, "error");
      }
   };

   const handleEdit = (student) => {
      setOperation("update");
      setStudentFormData({
         id: student.id,
         fullName: student.fullName,
         email: student.email,
      });
   };

   useEffect(() => {
      fetchStudents();
   }, []);

   return <StudentComp
      studentsList={studentsList}
      loading={loading}
      handleEdit={handleEdit}
      handleDelete={handleDelete}
      handleSubmit={handleSubmit}
      resetForm={resetForm}
      operation={operation}
      fetchStudents={fetchStudents}
      studentFormData={studentFormData}
      setStudentFormData={setStudentFormData}
   />
};

export default StudentContainer;