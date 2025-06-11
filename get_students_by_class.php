<?php
header('Content-Type: application/json');

include "db_connection.php";


if ($conn->connect_error) {
    echo json_encode(['error' => 'Database connection failed: ' . $conn->connect_error]);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $class_id = $_POST['classID'] ?? '';
    
    if (empty($class_id)) {
        echo json_encode(['error' => 'Class ID is required']);
        exit();
    }
    
    // استعلام لجلب الطلاب حسب الصف
    // تأكد من أن أسماء الجداول والحقول صحيحة حسب قاعدة بياناتك
    $stmt = $conn->prepare("SELECT student_id, name FROM students WHERE classID = ?");
    $stmt->bind_param("i", $class_id);
    
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $students = [];
        
        while ($row = $result->fetch_assoc()) {
            $students[] = [
                'student_id' => (int)$row['student_id'],
                'name' => $row['name']
            ];
        }
        
        echo json_encode($students);
    } else {
        echo json_encode(['error' => 'Query execution failed: ' . $stmt->error]);
    }
    
    $stmt->close();
} else {
    echo json_encode(['error' => 'Invalid request method']);
}

$conn->close();
?>