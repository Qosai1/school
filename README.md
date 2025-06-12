# 📚 School Management Android App

A complete Android application developed for the **Mobile Software Development (Comp4310)** course at **Birzeit University**. The app provides tailored functionalities for **Students**, **Teachers**, and **Registrars** in a unified School System.

---

## 🚀 Features

### 👩‍🎓 Student Module
- View class schedule dynamically
- View exam marks
- Submit assignments via interface

### 👨‍🏫 Teacher Module
- View own schedule
- Publish student grades
- Upload and assign homework

### 🧑‍💼 Registrar Module
- Add new students and subjects
- Build student & teacher schedules
- Manage users

---

## 🛠️ Technical Highlights

- Developed using **Java** in **Android Studio**
- Integrated with **MySQL database** via **PHP RESTful APIs**
- **Volley** for network operations
- **Shared Preferences** for local data storage
- Designed using:
  - Constraint, Linear, and Relative Layouts
  - ListView and RecyclerView
  - Dialogs, Spinners, DatePickers, TimePickers
- Multithreading support for responsiveness
- Full orientation lifecycle handling
- Centralized resource management:
  - `styles.xml` for consistent theming
  - `strings.xml` for all text elements

---

---

## 🔧 Installation & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/school-system-app.git
Import the project in Android Studio

Setup backend:

Host the included PHP scripts on your local server (e.g., XAMPP/Laragon)

Import the provided SQL schema to MySQL

Configure backend URL in the Android project (BaseURL.java or similar)

Run the app on an emulator or real device

🧑‍💻 Team Members
Name	
Qosai Badaha
Noor Hameeda
Deema Nemer
Dania Farraj 

Replace with your actual names and IDs

📁 Folder Structure (Important)
css
Copy
Edit
finalproject/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/finalproject/
│   │   │   │   ├── CreateStudentSchedule.java
│   │   │   │   ├── CreateTeacherSchedule.java
│   │   │   │   ├── ShowSubmissionsActivity.java
│   │   │   │   ├── AssignmentSubmissionsActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   └── ...
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── values/
│   │   │       └── drawable/
├── backend/
│   ├── add_student.php
│   ├── add_subject.php
│   ├── get_schedule.php
│   ├── post_assignment.php
│   └── ...
📌 Notes
