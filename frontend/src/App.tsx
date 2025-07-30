import { useState } from "react";
import StudentContainer from "./containers/StudentContainer";
import { BookOpen, Calendar, User } from "lucide-react";
import { UpdateMessage } from "./types/types";
import { HeaderMessage } from "./components/HeaderMessage";
import AttendanceContainer from "./containers/AttendanceContainer";
import CourseContainer from "./containers/CourseContainer";

function App() {
   const [activeTab, setActiveTab] = useState("students");
   const [message, setMessage] = useState<UpdateMessage | null>(null);

   const handleShowMessage = (text: string, type: string = "success") => {
      setMessage({ text, type });
      setTimeout(() => setMessage(null), 2000);
   };

   const tabChoiceList = [
      { id: "students", label: "Students", icon: User },
      { id: "courses", label: "Courses", icon: BookOpen },
      { id: "attendance", label: "Attendance", icon: Calendar },
   ]

   return (
      <div className="min-h-screen bg-purple-900 p-6">
         <div className="max-w-7xl mx-auto">
            <h1 className="text-3xl font-bold text-orange-400 mb-8 text-center">
               Student Attendance Management System
            </h1>

            {message && (
               <HeaderMessage msg={message} />
            )}

            <div className="flex space-x-3 mb-6 bg-purple-800 p-2 rounded-lg">
               {tabChoiceList.map(({ id, label, icon: Icon }) => (
                  <button
                     key={id}
                     onClick={() => setActiveTab(id)}
                     className={`flex items-center space-x-2 px-6 py-3 rounded-md transition-colors ${activeTab === id
                        ? "bg-orange-500 text-purple-900 shadow-lg font-semibold"
                        : "text-orange-300 hover:text-orange-200 hover:bg-purple-700"
                        }`}
                  >
                     <Icon size={18} />
                     <span>{label}</span>
                  </button>
               ))}
            </div>

            {activeTab === "students" && (
               <StudentContainer onShowMessage={handleShowMessage} />
            )}
            {activeTab === "courses" && (
               <CourseContainer showMessage={handleShowMessage} />
            )}
            {activeTab === "attendance" && (
               <AttendanceContainer showMessage={handleShowMessage} />
            )}
         </div>
      </div>
   );
}

export default App;
