<?php
// منع عرض أي أخطاء PHP في الاستجابة
error_reporting(0);
ini_set('display_errors', 0);

// إعداد headers
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Accept');
header('Content-Type: application/json; charset=utf-8');

// معالجة OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// التأكد من عدم وجود أي output قبل JSON
ob_clean();

try {
    include 'db_connection.php';

    // التحقق من طريقة الطلب
    if ($_SERVER['REQUEST_METHOD'] !== 'GET' && $_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Invalid request method");
    }
    
    // الحصول على assignment_id
    $assignment_id = null;
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        $assignment_id = isset($_GET['assignment_id']) ? intval($_GET['assignment_id']) : null;
    } else {
        $assignment_id = isset($_POST['assignment_id']) ? intval($_POST['assignment_id']) : null;
    }
    
    // التحقق من assignment_id
    if (!$assignment_id || $assignment_id <= 0) {
        throw new Exception("Valid Assignment ID is required");
    }
    
    // التحقق من الاتصال بقاعدة البيانات
    if (!isset($conn) || !$conn) {
        throw new Exception("Database connection failed");
    }
    
    // استعلام قاعدة البيانات
    $sql = "SELECT 
                s.student_id, 
                u.username AS student_name, 
                IFNULL(sub.attachment, '') as attachment, 
                DATE_FORMAT(sub.submitted_at, '%Y-%m-%d %H:%i') as submitted_at
            FROM submissions sub
            INNER JOIN students s ON sub.student_id = s.student_id
            INNER JOIN users u ON s.user_id = u.user_id
            WHERE sub.assignment_id = ?
            ORDER BY sub.submitted_at DESC";
    
    $stmt = $conn->prepare($sql);
    if (!$stmt) {
        throw new Exception("Database prepare failed: " . $conn->error);
    }
    
    $stmt->bind_param("i", $assignment_id);
    
    if (!$stmt->execute()) {
        throw new Exception("Database execute failed: " . $stmt->error);
    }
    
    $result = $stmt->get_result();
    $submissions = array();
    
    while ($row = $result->fetch_assoc()) {
        // التأكد من أن البيانات سليمة
        $submission = array(
            'student_id' => (int)$row['student_id'],
            'student_name' => (string)$row['student_name'],
            'attachment' => (string)$row['attachment'],
            'submitted_at' => (string)$row['submitted_at']
        );
        $submissions[] = $submission;
    }
    
    $stmt->close();
    $conn->close();
    
    // إرجاع JSON array
    echo json_encode($submissions, JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    // في حالة حدوث خطأ، إرجاع JSON object بالخطأ
    http_response_code(500);
    echo json_encode(array("error" => $e->getMessage()), JSON_UNESCAPED_UNICODE);
}
?>