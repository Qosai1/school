-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 11, 2025 at 09:38 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `schooll`
--

-- --------------------------------------------------------

--
-- Table structure for table `assignments`
--

CREATE TABLE `assignments` (
  `assignment_id` int(11) NOT NULL,
  `subject_id` int(11) DEFAULT NULL,
  `teacher_id` int(11) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `classID` int(15) NOT NULL,
  `attachment` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignments`
--

INSERT INTO `assignments` (`assignment_id`, `subject_id`, `teacher_id`, `title`, `description`, `due_date`, `classID`, `attachment`) VALUES
(1, 1, 1, 'sss', 'sssssssssssssss', '2025-12-15', 3, NULL),
(2, 1, 1, 'project', 'dddddddddd', '2025-05-30', 4, NULL),
(3, 1, 1, 'android project', 'ssssss', '2025-06-09', 3, NULL),
(4, 1, 2, 's', 'sdrdfv', '2025-06-04', 2, NULL),
(5, 1, 2, 'project', 'project', '2025-06-07', 2, '6847fd7f2717d_1749548415.jpeg|037BF395-03AF-410A-8955-89D2A01CA74E.jpeg'),
(6, 1, 2, 'ddd', 'ddd', '2025-06-26', 2, '684806fce5568_1749550844.docx|IOT.docx'),
(7, 1, 2, 'Assignment', 'aaa', '2025-06-05', 1, '68485d03a097f_1749572867.pdf|FinalProjectDescription.pdf'),
(8, 1, 2, 'ff', 'fffffffff', '2025-06-12', 2, '68496eb79678c_1749642935.jpeg|037BF395-03AF-410A-8955-89D2A01CA74E.jpeg'),
(9, 1, 2, 'ff', 'fffffffff', '2025-06-12', 2, '68496eb8928ee_1749642936.jpeg|037BF395-03AF-410A-8955-89D2A01CA74E.jpeg');

-- --------------------------------------------------------

--
-- Table structure for table `class`
--

CREATE TABLE `class` (
  `classID` int(15) NOT NULL,
  `className` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `class`
--

INSERT INTO `class` (`classID`, `className`) VALUES
(1, 'Grade 1\r\n'),
(2, 'Grade 2'),
(3, 'Grade 2'),
(4, 'Grade 4'),
(5, 'Grade 5'),
(6, 'Grade 6'),
(7, 'Grade 7'),
(8, 'Grade 8'),
(9, 'Grade 9'),
(10, 'Grade 10'),
(11, 'Grade 11'),
(12, 'Grade 12');

-- --------------------------------------------------------

--
-- Table structure for table `marks`
--

CREATE TABLE `marks` (
  `mark_id` int(11) NOT NULL,
  `student_id` int(11) DEFAULT NULL,
  `subject_id` int(11) DEFAULT NULL,
  `exam_name` varchar(100) DEFAULT NULL,
  `mark` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `marks`
--

INSERT INTO `marks` (`mark_id`, `student_id`, `subject_id`, `exam_name`, `mark`) VALUES
(5, 1, 1, 'quiz', 66.00),
(6, 2, 1, 'quiz', 90.00),
(9, 3, 2, 'MID', 50.00),
(10, 8, 2, 'QUIZ', 85.00),
(11, 8, 2, 'Quiz one', 99.00),
(12, 3, 2, 'Final', 88.00),
(13, 8, 2, 'Final', 55.00),
(14, 11, 2, 'Quiz', 88.00),
(15, 12, 2, 'Quiz', 99.00);

-- --------------------------------------------------------

--
-- Table structure for table `registrars`
--

CREATE TABLE `registrars` (
  `registrar_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `registrars`
--

INSERT INTO `registrars` (`registrar_id`, `name`, `email`, `phone`, `birth_date`, `gender`, `address`, `profile_picture`) VALUES
(1, 'Basil', 'basil@gmail.com', '0596545441', '1988-07-12', 'male', 'Ramallah', 'basil.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `schedules`
--

CREATE TABLE `schedules` (
  `schedule_id` int(11) NOT NULL,
  `class_name` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `schedules`
--

INSERT INTO `schedules` (`schedule_id`, `class_name`, `created_at`) VALUES
(1, '', '2025-05-24 13:14:38');

-- --------------------------------------------------------

--
-- Table structure for table `schedule_details`
--

CREATE TABLE `schedule_details` (
  `id` int(11) NOT NULL,
  `schedule_id` int(11) NOT NULL,
  `day` varchar(20) NOT NULL,
  `lecture_number` int(11) NOT NULL,
  `subject_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `student_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `classID` int(15) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `name`, `email`, `phone`, `birth_date`, `gender`, `address`, `classID`, `user_id`) VALUES
(1, 'Ali Ahmed', 'ali.ahmed@student.com', '0599123456', '2002-06-10', 'male', 'Rammallh', 5, 1),
(2, 'Sara Omar', 'sara.omar@student.com', '0599234567', '2003-03-22', 'female', 'Rafah', 7, 2),
(3, 'Khaled Nabil', 'khaled.nabil@student.com', '0599345678', '2001-11-05', 'male', 'Khan Younis', 2, 3),
(8, 'sereen', 'sereen@gmail.com', '0598741255', '2025-06-03', 'male', 'ramllah', 2, 8),
(9, 'Mohammad', 'mohd@gmail.com', '0598741236', '2025-06-07', 'female', 'Jenin', 8, 9),
(11, 'Jamila', 'jamila@gmail.com', '0598744111', '2025-06-04', 'female', 'Amman', 1, 14),
(12, 'xx', 'x@gmail.com', '0257411111', '2025-06-06', 'female', 'rrrr', 1, 16),
(13, 'ssss', 'ss@gmail.com', '8888888888', '2025-06-04', 'male', 'ddd', 4, 18),
(14, 'Amal', 'amal@gmail.com', '7777777777', '2025-06-05', 'female', 'Birzeit', 12, 19);

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE `subjects` (
  `subject_id` int(11) NOT NULL,
  `subject_name` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`subject_id`, `subject_name`) VALUES
(1, 'Arabic'),
(2, 'Islamic education\r\n'),
(3, 'Math'),
(4, 'English'),
(5, 'Technology\r\n'),
(6, 'Social studies\r\n'),
(7, 'Sport'),
(8, 'Art'),
(9, 'ٍٍScience'),
(12, 'France');

-- --------------------------------------------------------

--
-- Table structure for table `submissions`
--

CREATE TABLE `submissions` (
  `submission_id` int(11) NOT NULL,
  `assignment_id` int(11) DEFAULT NULL,
  `student_id` int(11) DEFAULT NULL,
  `attachment` varchar(255) NOT NULL,
  `submitted_at` date NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `submissions`
--

INSERT INTO `submissions` (`submission_id`, `assignment_id`, `student_id`, `attachment`, `submitted_at`) VALUES
(1, 4, 3, '341749500078.pdf', '2025-06-09'),
(2, 4, 8, '841749550987.pdf', '2025-06-10'),
(3, 5, 3, '351749641379.pdf', '2025-06-11'),
(4, 7, 11, '1171749643161.pdf', '2025-06-11');

-- --------------------------------------------------------

--
-- Table structure for table `teacher2class`
--

CREATE TABLE `teacher2class` (
  `teacher2classID` int(11) NOT NULL,
  `classID` int(15) NOT NULL,
  `teacher_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher2class`
--

INSERT INTO `teacher2class` (`teacher2classID`, `classID`, `teacher_id`) VALUES
(1, 1, 3),
(2, 2, 2),
(3, 1, 1),
(4, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

CREATE TABLE `teachers` (
  `teacher_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `subject_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teachers`
--

INSERT INTO `teachers` (`teacher_id`, `name`, `email`, `department`, `phone`, `birth_date`, `gender`, `address`, `bio`, `subject_id`, `user_id`) VALUES
(1, 'Badaha', 'badaha@gmail.com', 'Mathematics', '0597698931', '1980-06-15', 'male', 'Ramallah', 'Experienced Math teacher with a passion for teaching.', 1, 4),
(2, 'Ali', 'ali@gmail.com', 'Science', '0987654321', '1990-02-25', 'male', 'Ramallah', 'Physics and Chemistry enthusiast with over 10 years of teaching experience.', 2, 5),
(3, 'Ahmad', 'ahmad@gmail.com', 'History', '0597697931', '1985-11-10', 'male', 'Ramallah', 'A passionate History teacher dedicated to making history fun and engaging for students.', 3, 6),
(4, 'Majd', 'majd@gmail.com', 'Arabic', '0859741111', '2025-06-04', 'male', 'Ramallah', 'Teacher', 1, 11),
(7, 'Majdi', 'mjdi@gmail.com', 'Art', '1987456333', '2025-06-05', 'male', 'Amman', '', 8, 15),
(8, 'Rami', 'rami1@gmail.com', 'History', '1874444444', '2025-06-04', 'male', 'ramallah', '', 6, 17),
(9, 'sss', 'smai@gmail.com', 'fff', '7777777777', '2025-06-10', 'male', 'ffffff', '', 4, 20);

-- --------------------------------------------------------

--
-- Table structure for table `teacher_schedule`
--

CREATE TABLE `teacher_schedule` (
  `id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `day` varchar(20) NOT NULL,
  `lecture_number` int(11) NOT NULL,
  `class_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('student','teacher','registrar') NOT NULL,
  `reference_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `role`, `reference_id`) VALUES
(1, 'ali.ahmed', 'pass123', 'student', 1),
(2, 'sara.omar', 'pass123', 'student', 2),
(3, 'khaled.nabil', 'pass123', 'student', 3),
(4, 'Badaha', '123321', 'teacher', 1),
(5, 'Ali', '123321', 'teacher', 2),
(6, 'Ahmad', '123321', 'teacher', 3),
(7, 'Basil', '123321', 'registrar', 1),
(8, 'sereen', '123321', 'student', 8),
(9, 'mohammad', '123321', 'student', 9),
(11, 'majd', '123321', 'teacher', 4),
(14, 'jamila', '123321', 'student', 11),
(15, 'majdi461', '$2y$10$E78zFuBUKMudeu1YAsQKyuuzcqia5j/RRxSlollXnGEIMlpwPB5iS', 'teacher', 0),
(16, 'xx', '123321', 'student', 12),
(17, 'rami400', '$2y$10$WWmz7EFvJOIske4EW0FVo.e95AvtQlNKbPLFItz0TaIvBxmSg5IlG', 'teacher', 0),
(18, 'ssss', '123321', 'student', 13),
(19, 'amal', '123321', 'student', 14),
(20, 'sss143', '$2y$10$fWzGmXPsz.iH8udENICUU.DmIutyZkA3J8vBiEQuk71TArCvzL4wy', 'teacher', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `assignments`
--
ALTER TABLE `assignments`
  ADD PRIMARY KEY (`assignment_id`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `teacher_id` (`teacher_id`),
  ADD KEY `classID` (`classID`);

--
-- Indexes for table `class`
--
ALTER TABLE `class`
  ADD PRIMARY KEY (`classID`);

--
-- Indexes for table `marks`
--
ALTER TABLE `marks`
  ADD PRIMARY KEY (`mark_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `registrars`
--
ALTER TABLE `registrars`
  ADD PRIMARY KEY (`registrar_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `schedules`
--
ALTER TABLE `schedules`
  ADD PRIMARY KEY (`schedule_id`);

--
-- Indexes for table `schedule_details`
--
ALTER TABLE `schedule_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `schedule_id` (`schedule_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `classID` (`classID`),
  ADD KEY `aa` (`user_id`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`subject_id`);

--
-- Indexes for table `submissions`
--
ALTER TABLE `submissions`
  ADD PRIMARY KEY (`submission_id`),
  ADD KEY `assignment_id` (`assignment_id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `teacher2class`
--
ALTER TABLE `teacher2class`
  ADD PRIMARY KEY (`teacher2classID`),
  ADD KEY `Test2` (`classID`),
  ADD KEY `Test3` (`teacher_id`);

--
-- Indexes for table `teachers`
--
ALTER TABLE `teachers`
  ADD PRIMARY KEY (`teacher_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `Test6` (`subject_id`),
  ADD KEY `ss` (`user_id`);

--
-- Indexes for table `teacher_schedule`
--
ALTER TABLE `teacher_schedule`
  ADD PRIMARY KEY (`id`),
  ADD KEY `gg` (`teacher_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `assignments`
--
ALTER TABLE `assignments`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `class`
--
ALTER TABLE `class`
  MODIFY `classID` int(15) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `marks`
--
ALTER TABLE `marks`
  MODIFY `mark_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `registrars`
--
ALTER TABLE `registrars`
  MODIFY `registrar_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `schedules`
--
ALTER TABLE `schedules`
  MODIFY `schedule_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `schedule_details`
--
ALTER TABLE `schedule_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `subject_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `submissions`
--
ALTER TABLE `submissions`
  MODIFY `submission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `teacher2class`
--
ALTER TABLE `teacher2class`
  MODIFY `teacher2classID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `teachers`
--
ALTER TABLE `teachers`
  MODIFY `teacher_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `teacher_schedule`
--
ALTER TABLE `teacher_schedule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `assignments`
--
ALTER TABLE `assignments`
  ADD CONSTRAINT `assignments_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  ADD CONSTRAINT `assignments_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`),
  ADD CONSTRAINT `assignments_ibfk_3` FOREIGN KEY (`classID`) REFERENCES `class` (`classID`);

--
-- Constraints for table `marks`
--
ALTER TABLE `marks`
  ADD CONSTRAINT `marks_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `marks_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`);

--
-- Constraints for table `schedule_details`
--
ALTER TABLE `schedule_details`
  ADD CONSTRAINT `schedule_details_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `schedules` (`schedule_id`) ON DELETE CASCADE;

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `aa` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `classID` FOREIGN KEY (`classID`) REFERENCES `class` (`classID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `submissions`
--
ALTER TABLE `submissions`
  ADD CONSTRAINT `submissions_ibfk_1` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`assignment_id`),
  ADD CONSTRAINT `submissions_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`);

--
-- Constraints for table `teacher2class`
--
ALTER TABLE `teacher2class`
  ADD CONSTRAINT `Test2` FOREIGN KEY (`classID`) REFERENCES `class` (`classID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Test3` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `teachers`
--
ALTER TABLE `teachers`
  ADD CONSTRAINT `Test6` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `ss` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `teacher_schedule`
--
ALTER TABLE `teacher_schedule`
  ADD CONSTRAINT `gg` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
