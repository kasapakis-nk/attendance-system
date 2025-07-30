import { Plus, Edit, Trash2, RefreshCw } from "lucide-react";
import type { Student } from "../entities/Student";
import type { StudentFormData } from "../types/types";

interface StudentCompProps {
    studentsList: Student[];
    loading: boolean;
    handleEdit: (student: Student) => void;
    handleDelete: (id: number) => void;
    handleSubmit: () => void;
    resetForm: () => void;
    operation: string
    fetchStudents: () => void;
    studentFormData: StudentFormData;
    setStudentFormData: React.Dispatch<React.SetStateAction<StudentFormData>>;
}

const StudentComp = ({
    operation,
    studentsList,
    loading,
    handleEdit,
    handleDelete,
    handleSubmit,
    resetForm,
    fetchStudents,
    setStudentFormData,
    studentFormData
}: StudentCompProps) => {
    return (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-purple-800 p-6 rounded-lg shadow-lg border border-purple-600">
                <h2 className="text-xl font-semibold mb-4 text-orange-300">
                    {operation === "create" ? "Add" : "Edit"} Student
                </h2>

                <div className="space-y-4">
                    <input
                        type="text"
                        placeholder="Full Name"
                        value={studentFormData.fullName}
                        onChange={(e) =>
                            setStudentFormData({
                                ...studentFormData,
                                fullName: e.target.value,
                            })
                        }
                        className="w-full p-3 border border-purple-500 rounded-lg focus:ring-2 focus:ring-orange-400 bg-purple-700 text-orange-200 placeholder-orange-300"
                    />
                    <input
                        type="email"
                        placeholder="Email"
                        value={studentFormData.email}
                        onChange={(e) =>
                            setStudentFormData({ ...studentFormData, email: e.target.value })
                        }
                        className="w-full p-3 border border-purple-500 rounded-lg focus:ring-2 focus:ring-orange-400 bg-purple-700 text-orange-200 placeholder-orange-300"
                    />

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
                            {operation === "create" ? "Add Student" : "Update Student"}
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
            </div>

            <div className="bg-purple-800 p-6 rounded-lg shadow-lg border border-purple-600">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-xl font-semibold text-orange-300">
                        Students ({studentsList.length})
                    </h2>
                    <button
                        onClick={fetchStudents}
                        className="inline-flex items-center p-3 text-orange-300 hover:text-orange-200 hover:bg-purple-700 rounded-lg transition-colors"
                        title="Refresh Students"
                    >
                        <RefreshCw size={18} className="inline" />
                        <span className="ml-2 text-sm">Refresh</span>
                    </button>
                </div>

                <div className="space-y-3 max-h-96 overflow-y-auto">
                    {studentsList.length === 0 ? (
                        <div className="text-center py-8 text-orange-300">
                            No students found. Add some data!
                        </div>
                    ) : (
                        studentsList.map((student) => (
                            <div key={student.id} className="p-4 border border-purple-600 rounded-lg bg-purple-700">
                                <div className="flex justify-between items-start">
                                    <div>
                                        <h3 className="font-medium text-orange-200">{student.fullName}</h3>
                                        <p className="text-orange-300">{student.email}</p>
                                        <p className="text-sm text-orange-400">
                                            ID: {student.id}
                                        </p>
                                    </div>
                                    <div className="flex space-x-3">
                                        <button
                                            onClick={() => handleEdit(student)}
                                            className="p-2 text-orange-400 hover:bg-purple-600 rounded transition-colors flex items-center"
                                            title="Edit Student"
                                        >
                                            <Edit size={16} />
                                            <span className="ml-1 text-xs">Edit</span>
                                        </button>
                                        <button
                                            onClick={() => handleDelete(student.id)}
                                            className="p-2 text-red-400 hover:bg-purple-600 rounded transition-colors flex items-center"
                                            title="Delete Student"
                                        >
                                            <Trash2 size={16} />
                                            <span className="ml-1 text-xs">Delete</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}

export default StudentComp