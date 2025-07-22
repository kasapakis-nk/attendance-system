# 📚 Student Attendance Management System

A full-stack web application for managing student attendance with a Java servlet backend and React frontend. Track students, courses, and attendance records with a clean, modern interface.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-F8DC75?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)

## 🚀 Features

- **Student Management**: Create, read, update, and delete student records
- **Course Management**: Manage course information and instructors
- **Attendance Tracking**: Record and monitor student attendance per course
- **RESTful API**: Clean REST endpoints for all operations
- **Modern UI**: Responsive React frontend with Tailwind CSS
- **Real-time Updates**: Live data synchronization between frontend and backend

## 🏗️ Architecture

### Backend Structure
```
src/
├── com/AttendanceManagementSystem/
│   ├── model/
│   │   ├── Student.java           # Student entity
│   │   ├── Course.java            # Course entity
│   │   └── AttendanceRecord.java  # Attendance record entity
│   ├── servlet/
│   │   ├── StudentServlet.java    # Student REST endpoints
│   │   ├── CourseServlet.java     # Course REST endpoints
│   │   └── AttendanceServlet.java # Attendance REST endpoints
│   └── storage/
│       └── DataStore.java         # In-memory data storage
```

### Frontend Structure
```
src/
├── App.jsx                    # Main app component
├── AttendanceApp.jsx         # Main application with tabs
├── StudentContainer.jsx      # Student management UI
├── CourseContainer.jsx       # Course management UI
├── AttendanceContainer.jsx   # Attendance management UI
├── index.js                  # React entry point
└── main.jsx                  # Vite entry point
```

## 📋 Prerequisites

- **Java 8+**
- **Apache Tomcat 9.0+**
- **Node.js 16+**
- **npm or yarn**

## ⚙️ Installation & Setup

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd student-attendance-system
   ```

2. **Configure Tomcat Path**
   ```bash
   # Edit compile.bat and update the TOMCAT_PATH
   set TOMCAT_PATH=C:\Your\Path\To\Tomcat\apache-tomcat-9.0.106
   ```

3. **Compile Java Files**
   ```bash
   # Windows
   compile.bat
   
   # Manual compilation
   javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar" -d WEB-INF\classes src\com\AttendanceManagementSystem\model\*.java
   javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar;WEB-INF\classes" -d WEB-INF\classes src\com\AttendanceManagementSystem\storage\*.java
   javac -cp "%TOMCAT_PATH%\lib\servlet-api.jar;WEB-INF\classes" -d WEB-INF\classes src\com\AttendanceManagementSystem\servlet\*.java
   ```

4. **Deploy to Tomcat**
   - Copy the entire project folder to `%TOMCAT_PATH%\webapps\attendance-system\`
   - Start Tomcat server
   - Backend will be available at: `http://localhost:8080/attendance-system`

### Frontend Setup

1. **Install Dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

2. **Start Development Server**
   ```bash
   npm start
   # or
   yarn start
   ```

3. **Frontend will be available at**: `http://localhost:3000`

## 🔌 API Endpoints

### 👥 Student Endpoints

| Method | Endpoint | Description | Frontend Trigger |
|--------|----------|-------------|------------------|
| `GET` | `/students/` | Get all students | **Refresh Button** in Students tab |
| `POST` | `/students/` | Create new student | **Add Student Button** |
| `GET` | `/students/{id}` | Get student by ID | *Not directly used in UI* |
| `PUT` | `/students/{id}` | Update student | **Update Student Button** (after Edit) |
| `DELETE` | `/students/{id}` | Delete student | **Delete Button** (trash icon) |

#### Request/Response Examples

**Create Student (POST /students/)**
```json
// Request Body
{
  "fullName": "John Doe",
  "email": "john.doe@email.com",
  "registeredCourses": []
}

// Response
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john.doe@email.com",
  "registeredCourses": []
}
```

### 📚 Course Endpoints

| Method | Endpoint | Description | Frontend Trigger |
|--------|----------|-------------|------------------|
| `GET` | `/courses/` | Get all courses | **Refresh Button** in Courses tab |
| `POST` | `/courses/` | Create new course | **Add Course Button** |
| `GET` | `/courses/{id}` | Get course by ID | *Not directly used in UI* |
| `PUT` | `/courses/{id}` | Update course | **Update Course Button** (after Edit) |
| `DELETE` | `/courses/{id}` | Delete course | **Delete Button** (trash icon) |

#### Request/Response Examples

**Create Course (POST /courses/)**
```json
// Request Body
{
  "name": "Java Programming",
  "instructor": "Dr. Smith"
}

// Response
{
  "id": 1,
  "name": "Java Programming",
  "instructor": "Dr. Smith"
}
```

### 📅 Attendance Endpoints

| Method | Endpoint | Description | Frontend Trigger |
|--------|----------|-------------|------------------|
| `GET` | `/attendance/` | Get all attendance records | **Refresh Button** in Attendance tab |
| `GET` | `/attendance/?studentId={id}` | Filter by student | **Student Filter Dropdown** |
| `GET` | `/attendance/?courseId={id}` | Filter by course | **Course Filter Dropdown** |
| `GET` | `/attendance/?studentId={id}&courseId={id}` | Filter by both | *Combined filters* |
| `POST` | `/attendance/` | Record attendance | **Record Attendance Button** |
| `GET` | `/attendance/{id}` | Get attendance by ID | *Not directly used in UI* |
| `PUT` | `/attendance/{id}` | Update attendance | **Update Attendance Button** (after Edit) |
| `DELETE` | `/attendance/{id}` | Delete attendance | **Delete Button** (trash icon) |

#### Request/Response Examples

**Record Attendance (POST /attendance/)**
```json
// Request Body
{
  "studentId": 1,
  "courseId": 1,
  "date": "2024-01-15",
  "present": true
}

// Response
{
  "id": 1,
  "studentId": 1,
  "courseId": 1,
  "date": "2024-01-15",
  "present": true
}
```

## 🎨 Frontend UI Guide

### Navigation Tabs
- **Students Tab**: Manage student records
- **Courses Tab**: Manage course information  
- **Attendance Tab**: Record and view attendance

### UI Features

#### Students Tab
- **Add/Edit Form**: Input fields for name and email
- **Student List**: Display all students with edit/delete actions
- **Add Student Button**: Creates new student (calls `POST /students/`)
- **Edit Button**: Loads student data into form (triggers `PUT /students/{id}`)
- **Delete Button**: Removes student (calls `DELETE /students/{id}`)
- **Refresh Button**: Reloads student list (calls `GET /students/`)

#### Courses Tab
- **Add/Edit Form**: Input fields for course name and instructor
- **Course List**: Display all courses with edit/delete actions
- **Add Course Button**: Creates new course (calls `POST /courses/`)
- **Edit Button**: Loads course data into form (triggers `PUT /courses/{id}`)
- **Delete Button**: Removes course (calls `DELETE /courses/{id}`)
- **Refresh Button**: Reloads course list (calls `GET /courses/`)

#### Attendance Tab
- **Record Form**: Dropdowns for student/course, date picker, present/absent radio buttons
- **Filter Section**: Student and course dropdowns for filtering records
- **Attendance List**: Display all records with edit/delete actions
- **Record Attendance Button**: Creates attendance record (calls `POST /attendance/`)
- **Update Attendance Button**: Updates existing record (calls `PUT /attendance/{id}`)
- **Delete Button**: Removes attendance record (calls `DELETE /attendance/{id}`)
- **Student Filter**: Filters records by student (calls `GET /attendance/?studentId={id}`)
- **Course Filter**: Filters records by course (calls `GET /attendance/?courseId={id}`)
- **Refresh Button**: Reloads all attendance records (calls `GET /attendance/`)

## 🔧 Configuration

### Environment Variables
- `API_BASE`: Set to your backend URL (default: `http://localhost:8080/attendance-system`)

### CORS Configuration
The backend currently doesn't include CORS headers. For production, add CORS filter to your servlets or use a reverse proxy.

## 🗃️ Sample Data

The system initializes with sample data:

**Students:**
- Mitsos Karatasou (mitsos.kara@gmail.com)
- Maria Eleutheriou (maria.ele@email.com)  
- Arnold Schwarz (arnie.sch@email.com)

**Courses:**
- Java Programming (Dr. John)
- Data Structures (Prof. Davis)
- Web Development (Sir Mathews)

**Attendance Records:**
- Sample records linking students to courses

## 🚨 Error Handling

The API returns appropriate HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `404` - Not Found
- `500` - Internal Server Error

Error responses include descriptive messages:
```json
{
  "error": "Student not found with ID: 999"
}
```

## 🔮 Future Enhancements

- [ ] Add proper JSON library (Jackson/Gson)
- [ ] Implement CORS support
- [ ] Add authentication and authorization
- [ ] Database persistence (replace in-memory storage)
- [ ] Input validation and sanitization
- [ ] Logging framework integration
- [ ] Unit and integration tests
- [ ] Docker containerization
- [ ] Attendance reporting and analytics

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Built with ❤️ for educational purposes**