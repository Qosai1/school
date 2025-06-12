<?php
header('Content-Type: application/json');

// إعدادات قاعدة البيانات
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "schooll";

// التحقق من وجود teacher_id في الطلب
if (!isset($_POST['teacher_id'])) {
    echo json_encode(array("status" => "error", "message" => "Teacher ID is required"));
    exit;
}

$teacher_id = $_POST['teacher_id'];

try {
    // الاتصال بقاعدة البيانات
    $conn = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // جلب subject_id الخاص بالمعلم
    $stmt = $conn->prepare("SELECT subject_id FROM teachers WHERE id = :teacher_id");
    $stmt->bindParam(':teacher_id', $teacher_id);
    $stmt->execute();
    
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if ($result) {
        echo json_encode(array(
            "status" => "success", 
            "subject_id" => $result['subject_id']
        ));
    } else {
        echo json_encode(array("status" => "error", "message" => "Teacher not found"));
    }
    
} catch(PDOException $e) {
    echo json_encode(array("status" => "error", "message" => "Database error: " . $e->getMessage()));
}

$conn = null;
?>