<?php
header('Content-Type: application/json');
include "db_connection.php";

// التحقق من وجود البيانات الأساسية
if (!isset($_POST['title']) || !isset($_POST['description']) || 
    !isset($_POST['due_date']) || !isset($_POST['classID']) || 
    !isset($_POST['teacher_id']) || !isset($_POST['subject_id'])) {
    echo json_encode(["status" => "error", "message" => "Missing required fields"]);
    exit;
}

$title = $_POST['title'];
$description = $_POST['description'];
$due_date = $_POST['due_date'];
$classID = intval($_POST['classID']);
$teacher_id = intval($_POST['teacher_id']);
$subject_id = intval($_POST['subject_id']);

// التحقق من صحة البيانات
if (empty($title) || empty($description) || empty($due_date) || $classID <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid input data"]);
    exit;
}

// معالجة الملف المرفق (اختياري)
$attachment_path = null;
$has_attachment = isset($_POST['has_attachment']) && $_POST['has_attachment'] == '1';

if ($has_attachment) {
    if (!isset($_POST['file_name']) || !isset($_POST['file_data'])) {
        echo json_encode(["status" => "error", "message" => "File data missing"]);
        exit;
    }
    
    $file_name = $_POST['file_name'];
    $file_data = base64_decode($_POST['file_data']);
    
    if ($file_data === false) {
        echo json_encode(["status" => "error", "message" => "Invalid file data"]);
        exit;
    }
    
    // إنشاء مجلد للملفات إذا لم يكن موجود
    $uploads_dir = "uploads/assignments/";
    if (!file_exists($uploads_dir)) {
        mkdir($uploads_dir, 0777, true);
    }
    
    // إنشاء اسم ملف فريد
    $file_extension = pathinfo($file_name, PATHINFO_EXTENSION);
    $unique_filename = uniqid() . '_' . time() . '.' . $file_extension;
    $attachment_path = $uploads_dir . $unique_filename;
    
    // حفظ الملف
    if (file_put_contents($attachment_path, $file_data) === false) {
        echo json_encode(["status" => "error", "message" => "Failed to save file"]);
        exit;
    }
    
    // حفظ الاسم الأصلي للملف أيضاً
    $attachment_path = $unique_filename . '|' . $file_name; // اسم الملف الجديد|الاسم الأصلي
}

// إدراج البيانات في قاعدة البيانات
$sql = "INSERT INTO assignments (title, description, due_date, classID, teacher_id, subject_id, attachment) 
        VALUES (?, ?, ?, ?, ?, ?, ?)";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("sssiiss", $title, $description, $due_date, $classID, $teacher_id, $subject_id, $attachment_path);

if ($stmt->execute()) {
    $response = [
        "status" => "success", 
        "message" => "Assignment added successfully"
    ];
    
    if ($has_attachment) {
        $response["attachment"] = $file_name;
    }
    
    echo json_encode($response);
} else {
    // إذا فشل الحفظ في قاعدة البيانات، احذف الملف المرفوع
    if ($has_attachment && file_exists($uploads_dir . explode('|', $attachment_path)[0])) {
        unlink($uploads_dir . explode('|', $attachment_path)[0]);
    }
    
    echo json_encode(["status" => "error", "message" => "Execute failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>