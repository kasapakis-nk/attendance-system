export type UpdateMessage = {
   text: string;
   type?: string;
};

export type AttendanceFormData = {
   id: string;
   studentId: string;
   courseId: string;
   date: string;
   present: boolean;
};

export type StudentFormData = {
   id: string;
   fullName: string;
   email: string;
};

export type CourseFormData = {
   id: string;
   name: string;
   instructor: string;
};