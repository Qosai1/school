<?php
header('Content-Type: application/json');

include "db_connection.php";


if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed']);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $exam_name = $_POST['exam_name'] ?? '';
    $subject_id = $_POST['subject_id'] ?? '';
    $marks_data = $_POST['marks_data'] ?? '';
    
    if (empty($exam_name) || empty($subject_id) || empty($marks_data)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
        exit();
    }
    
    $marks_array = json_decode($marks_data, true);
    if (!$marks_array) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid marks data']);
        exit();
    }
    
    // بدء المعاملة
    $conn->begin_transaction();
    
    try {
        $stmt = $conn->prepare("INSERT INTO marks (student_id, subject_id, exam_name, mark) VALUES (?, ?, ?, ?)");
        
        foreach ($marks_array as $mark_item) {
            $student_id = $mark_item['student_id'];
            $mark = $mark_item['mark'];
            
            if (!is_numeric($mark)) {
                throw new Exception("Invalid mark value for student ID: $student_id");
            }
            
            $stmt->bind_param("iiss", $student_id, $subject_id, $exam_name, $mark);
            
            if (!$stmt->execute()) {
                throw new Exception("Failed to insert mark for student ID: $student_id");
            }
        }
        
        // تأكيد المعاملة
        $conn->commit();
        echo json_encode(['status' => 'success', 'message' => 'Marks added successfully']);
        
    } catch (Exception $e) {
        // إلغاء المعاملة في حالة الخطأ
        $conn->rollback();
        echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
    }
    
    $stmt->close();
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
}

$conn->close();
?>