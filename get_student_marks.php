<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

// إعدادات قاعدة البيانات
$servername = "localhost";
$username = "root"; // أو اسم المستخدم الخاص بك
$password = ""; // أو كلمة المرور الخاصة بك
$dbname = "schooll"; // غيّر اسم قاعدة البيانات

try {
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // قراءة البيانات المرسلة
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!isset($input['student_id'])) {
        echo json_encode(['status' => 'error', 'message' => 'Student ID required']);
        exit();
    }
    
    $student_id = $input['student_id'];
    
    // استعلام لجلب علامات الطالب مع معلومات المادة
    $sql = "SELECT 
                m.mark_id,
                m.exam_name,
                m.mark,
                s.subject_name,
                s.subject_id
            FROM marks m 
            LEFT JOIN subjects s ON m.subject_id = s.subject_id 
            WHERE m.student_id = :student_id 
            ORDER BY s.subject_name, m.exam_name";
    
    $stmt = $pdo->prepare($sql);
    $stmt->bindParam(':student_id', $student_id, PDO::PARAM_INT);
    $stmt->execute();
    
    $marks = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    if (count($marks) > 0) {
        echo json_encode([
            'status' => 'success',
            'marks' => $marks
        ]);
    } else {
        echo json_encode([
            'status' => 'success',
            'marks' => [],
            'message' => 'No marks found for this student'
        ]);
    }
    
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>