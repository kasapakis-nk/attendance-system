import { Plus, Edit, Trash2, RefreshCw, Search } from "lucide-react";
import { AttendanceFormData } from "../types/types";
import type { Student } from "../entities/Student";
import type { Course } from "../entities/Course";
import type { AttendanceRecord } from "../entities/AttendanceRecord";

interface AttendanceContainerProps {
   operation: string
   formData: AttendanceFormData
   studentsList: Student[]
   coursesList: Course[]
   attendanceRecords: AttendanceRecord[]
   onFormChange: (data: AttendanceFormData) => void
   resetForm: () => void
   loading: boolean
   fetchAttendance: (studentId?: string, courseId?: string) => void
   handleDelete: (id: number) => void
   handleEdit: (record: AttendanceRecord) => void
   handleSubmit: () => void
}

const AttendanceComp = ({
   operation,
   formData,
   studentsList,
   coursesList,
   attendanceRecords,
   onFormChange,
   resetForm,
   loading,
   fetchAttendance,
   handleDelete,
   handleEdit,
   handleSubmit
}: AttendanceContainerProps) => {
   return (
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
         <div className="bg-purple-800 p-6 rounded-lg shadow-lg border border-purple-600">
            <h2 className="text-xl font-semibold mb-4 text-orange-300">
               {operation === "create" ? "Record" : "Edit"} Attendance
            </h2>

            <div className="space-y-4">
               <select
                  value={formData.studentId}
                  onChange={(e) =>
                     onFormChange({
                        ...formData,
                        studentId: e.target.value,
                     })
                  }
                  className="w-full p-3 border border-purple-500 rounded-lg focus:ring-2 focus:ring-orange-400 bg-purple-700 text-orange-200"
               >
                  <option value="" className="bg-purple-700 text-orange-300">
                     Select Student
                  </option>
                  {studentsList.map((student) => (
                     <option
                        key={student.id}
                        value={student.id}
                        className="bg-purple-700 text-orange-200"
                     >
                        {student.fullName}
                     </option>
                  ))}
               </select>

               <select
                  value={formData.courseId}
                  onChange={(e) =>
                     onFormChange({
                        ...formData,
                        courseId: e.target.value,
                     })
                  }
                  className="w-full p-3 border border-purple-500 rounded-lg focus:ring-2 focus:ring-orange-400 bg-purple-700 text-orange-200"
               >
                  <option value="" className="bg-purple-700 text-orange-300">
                     Select Course
                  </option>
                  {coursesList.map((course) => (
                     <option
                        key={course.id}
                        value={course.id}
                        className="bg-purple-700 text-orange-200"
                     >
                        {course.name}
                     </option>
                  ))}
               </select>

               <input
                  type="date"
                  value={formData.date}
                  onChange={(e) =>
                     onFormChange({ ...formData, date: e.target.value })
                  }
                  className="w-full p-3 border border-purple-500 rounded-lg focus:ring-2 focus:ring-orange-400 bg-purple-700 text-orange-200"
               />

               <div className="flex space-x-6">
                  <label className="flex items-center text-orange-300">
                     <input
                        type="radio"
                        name="present"
                        checked={formData.present === true}
                        onChange={() => onFormChange({ ...formData, present: true })}
                        className="mr-2 text-orange-500 focus:ring-orange-400"
                     />
                     Present
                  </label>
                  <label className="flex items-center text-orange-300">
                     <input
                        type="radio"
                        name="present"
                        checked={formData.present === false}
                        onChange={() => onFormChange({ ...formData, present: false })}
                        className="mr-2 text-orange-500 focus:ring-orange-400"
                     />
                     Absent
                  </label>
               </div>

               <div className="flex space-x-4">
                  <button
                     onClick={handleSubmit}
                     disabled={loading}
                     className="flex-1 bg-orange-500 text-purple-900 py-3 px-4 rounded-lg hover:bg-orange-400 disabled:opacity-50 flex items-center justify-center font-semibold transition-colors"
                  >
                     {loading ? (
                        <RefreshCw className="animate-spin mr-2" size={18} />
                     ) : (
                        <Plus className="mr-2" size={18} />
                     )}
                     {operation === "create"
                        ? "Record Attendance"
                        : "Update Attendance"}
                  </button>
                  {operation === "update" && (
                     <button
                        onClick={resetForm}
                        className="px-6 py-3 bg-purple-600 text-orange-300 rounded-lg hover:bg-purple-500 transition-colors font-semibold border border-purple-500"
                     >
                        Cancel
                     </button>
                  )}
               </div>
            </div>

            <div className="mt-6 pt-6 border-t border-purple-600">
               <h3 className="text-lg font-medium mb-3 flex items-center text-orange-300">
                  <Search className="mr-2" size={18} />
                  Filter Attendance
               </h3>
               <div className="flex space-x-3">
                  <select
                     onChange={(e) => fetchAttendance(e.target.value, "")}
                     className="flex-1 p-2 border border-purple-500 rounded-lg bg-purple-700 text-orange-200"
                  >
                     <option value="" className="bg-purple-700 text-orange-300">
                        All Students
                     </option>
                     {studentsList.map((student) => (
                        <option
                           key={student.id}
                           value={student.id}
                           className="bg-purple-700 text-orange-200"
                        >
                           {student.fullName}
                        </option>
                     ))}
                  </select>
                  <select
                     onChange={(e) => fetchAttendance("", e.target.value)}
                     className="flex-1 p-2 border border-purple-500 rounded-lg bg-purple-700 text-orange-200"
                  >
                     <option value="" className="bg-purple-700 text-orange-300">
                        All Courses
                     </option>
                     {coursesList.map((course) => (
                        <option
                           key={course.id}
                           value={course.id}
                           className="bg-purple-700 text-orange-200"
                        >
                           {course.name}
                        </option>
                     ))}
                  </select>
               </div>
            </div>
         </div>

         <div className="bg-purple-800 p-6 rounded-lg shadow-lg border border-purple-600">
            <div className="flex items-center justify-between mb-4">
               <h2 className="text-xl font-semibold text-orange-300">
                  Attendance Records ({attendanceRecords.length})
               </h2>
               <button
                  onClick={() => fetchAttendance()}
                  className="flex items-center p-3 text-orange-300 hover:text-orange-200 hover:bg-purple-700 rounded-lg transition-colors"
                  title="Refresh Attendance"
               >
                  <RefreshCw size={18} />
                  <span className="ml-2 text-sm">Refresh</span>
               </button>
            </div>

            <div className="space-y-3 max-h-96 overflow-y-auto">
               {attendanceRecords.length === 0 ? (
                  <div className="text-center py-8 text-orange-300">
                     No attendance records found. Record some attendance!
                  </div>
               ) : (
                  attendanceRecords.map((record) => {
                     const student = studentsList.find((s) => s.id === record.studentId);
                     const course = coursesList.find((c) => c.id === record.courseId);
                     return (
                        <div
                           key={record.id}
                           className="p-4 border border-purple-600 rounded-lg bg-purple-700"
                        >
                           <div className="flex justify-between items-start">
                              <div>
                                 <h3 className="font-medium text-orange-200">
                                    {student?.fullName ||
                                       `Student ${record.studentId}`}
                                 </h3>
                                 <p className="text-orange-300">
                                    {course?.name || `Course ${record.courseId}`}
                                 </p>
                                 <p className="text-sm text-orange-400">
                                    {record.date} -
                                    <span
                                       className={`ml-1 ${record.present
                                          ? "text-green-400"
                                          : "text-red-400"
                                          }`}
                                    >
                                       {record.present ? "Present" : "Absent"}
                                    </span>
                                 </p>
                                 <p className="text-sm text-orange-400">
                                    ID: {record.id}
                                 </p>
                              </div>
                              <div className="flex space-x-3">
                                 <button
                                    onClick={() => handleEdit(record)}
                                    className="p-2 text-orange-400 hover:bg-purple-600 rounded transition-colors flex items-center"
                                    title="Edit Record"
                                 >
                                    <Edit size={16} />
                                    <span className="ml-1 text-xs">Edit</span>
                                 </button>
                                 <button
                                    onClick={() => handleDelete(record.id)}
                                    className="p-2 text-red-400 hover:bg-purple-600 rounded transition-colors flex items-center"
                                    title="Delete Record"
                                 >
                                    <Trash2 size={16} />
                                    <span className="ml-1 text-xs">Delete</span>
                                 </button>
                              </div>
                           </div>
                        </div>
                     );
                  })
               )}
            </div>
         </div>
      </div>
   );
};

export default AttendanceComp;
