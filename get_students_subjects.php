<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// إعدادات قاعدة البيانات
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "schooll";

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // قراءة البيانات من الطلب
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['student_id'])) {
        echo json_encode(['status' => 'error', 'message' => 'Student ID is required']);
        exit;
    }
    
    $student_id = $input['student_id'];
    
    // استعلام للحصول على المواد الخاصة بالطالب
    // يمكنك تعديل هذا الاستعلام حسب هيكل قاعدة البيانات الخاصة بك
    $sql = "SELECT DISTINCT s.subject_id, s.subject_name, s.description 
            FROM subjects s 
            INNER JOIN student_subjects ss ON s.subject_id = ss.subject_id 
            WHERE ss.student_id = :student_id 
            ORDER BY s.subject_name";
    
    $stmt = $pdo->prepare($sql);
    $stmt->bindParam(':student_id', $student_id, PDO::PARAM_INT);
    $stmt->execute();
    
    $subjects = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($subjects) > 0) {
        echo json_encode([
            'status' => 'success',
            'subjects' => $subjects
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'subjects' => [],
            'message' => 'No subjects found'
        ]);
    }
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>