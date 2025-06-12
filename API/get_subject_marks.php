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
    
    if (!isset($input['student_id']) || !isset($input['subject_id'])) {
        echo json_encode(['status' => 'error', 'message' => 'Student ID and Subject ID are required']);
        exit;
    }
    
    $student_id = $input['student_id'];
    $subject_id = $input['subject_id'];
    
    // استعلام للحصول على العلامات لمادة معينة
    $sql = "SELECT m.mark_id, m.exam_name, m.mark, m.subject_id, s.subject_name 
            FROM marks m 
            INNER JOIN subjects s ON m.subject_id = s.subject_id 
            WHERE m.student_id = :student_id AND m.subject_id = :subject_id 
            ORDER BY m.exam_name";
    
    $stmt = $pdo->prepare($sql);
    $stmt->bindParam(':student_id', $student_id, PDO::PARAM_INT);
    $stmt->bindParam(':subject_id', $subject_id, PDO::PARAM_INT);
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
            'message' => 'No marks found for this subject'
        ]);
    }
    
} catch(PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>