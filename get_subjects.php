<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// إعدادات قاعدة البيانات
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "schooll"; // غير اسم قاعدة البيانات

try {
    // الاتصال بقاعدة البيانات
    $pdo = new PDO("mysql:host=$servername;dbname=$dbname;charset=utf8mb4", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // استعلام لجلب جميع المواد
    $stmt = $pdo->prepare("SELECT subject_id, subject_name FROM subjects ORDER BY subject_name ASC");
    $stmt->execute();
    
    $subjects = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    // إرسال النتيجة كـ JSON
    echo json_encode($subjects, JSON_UNESCAPED_UNICODE);
    
} catch(PDOException $e) {
    // في حالة الخطأ
    http_response_code(500);
    echo json_encode([
        "error" => "خطأ في قاعدة البيانات",
        "details" => $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}

$pdo = null;
?>