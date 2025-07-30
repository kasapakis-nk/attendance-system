import { useState, useEffect } from "react";
import type { CourseFormData } from "../types/types";
import type { Course } from "../entities/Course";
import CourseComp from "../components/CourseComp";

const API_BASE = "http://localhost:8080/attendance-system";

const CourseContainer = ({ showMessage }) => {
   const [courseList, setCourseList] = useState<Course[]>([]);
   const [loading, setLoading] = useState(false);
   const [operation, setOperation] = useState("create");
   const [courseFormData, setCourseFormData] = useState<CourseFormData>({
      id: "",
      name: "",
      instructor: "",
   });

   const resetForm = () => {
      setCourseFormData({
         id: "",
         name: "",
         instructor: "",
      });
      setOperation("create");
   };

   const fetchCourses = async () => {
      setLoading(true);
      try {
         const response = await fetch(`${API_BASE}/courses/`);
         if (response.ok) {
            const result = await response.json();
            setCourseList(result);
         }
      } catch (error) {
         showMessage(`Error fetching courses: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const handleSubmit = async () => {
      setLoading(true);

      try {
         let url = `${API_BASE}/courses/`;
         let method = "POST";

         if (operation === "update") {
            url += courseFormData.id;
            method = "PUT";
         }

         const body = {
            name: courseFormData.name,
            instructor: courseFormData.instructor,
         };

         const response = await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body),
         });

         if (response.ok) {
            showMessage(
               `Course ${operation === "create" ? "created" : "updated"
               } successfully!`
            );
            resetForm();
            fetchCourses();
         } else {
            const error = await response.json();
            showMessage(error.error || "Operation failed", "error");
         }
      } catch (error) {
         showMessage(`Error: ${error.message}`, "error");
      }
      setLoading(false);
   };

   const handleDelete = async (id) => {
      if (!window.confirm("Delete this course?")) return;

      try {
         const response = await fetch(`${API_BASE}/courses/${id}`, {
            method: "DELETE",
         });
         if (response.ok) {
            showMessage("Course deleted successfully!");
            fetchCourses();
         } else {
            const error = await response.json();
            showMessage(error.error || "Delete failed", "error");
         }
      } catch (error) {
         showMessage(`Error: ${error.message}`, "error");
      }
   };

   const handleEdit = (course) => {
      setOperation("update");
      setCourseFormData({
         id: course.id,
         name: course.name,
         instructor: course.instructor,
      });
   };

   useEffect(() => {
      fetchCourses();
   }, []);

   return (
      <CourseComp
         courseList={courseList}
         loading={loading}
         handleEdit={handleEdit}
         handleDelete={handleDelete}
         handleSubmit={handleSubmit}
         resetForm={resetForm}
         operation={operation}
         fetchCourses={fetchCourses}
         courseFormData={courseFormData}
         setCourseFormData={setCourseFormData}
      />
   );
};

export default CourseContainer;
