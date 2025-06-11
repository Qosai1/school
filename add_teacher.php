<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
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
    
    // قراءة البيانات المرسلة
    $input = json_decode(file_get_contents('php://input'), true);
    
    if (!$input) {
        throw new Exception("لا توجد بيانات مرسلة");
    }
    
    // التحقق من وجود البيانات المطلوبة
    $required_fields = ['name', 'email', 'phone', 'birth_date', 'gender', 'address', 'department', 'subject_id'];
    foreach ($required_fields as $field) {
        if (!isset($input[$field]) || empty(trim($input[$field]))) {
            throw new Exception("الحقل مطلوب: " . $field);
        }
    }
    
    // استخراج البيانات
    $name = trim($input['name']);
    $email = trim($input['email']);
    $phone = trim($input['phone']);
    $birth_date = $input['birth_date'];
    $gender = $input['gender'];
    $address = trim($input['address']);
    $department = trim($input['department']);
    $bio = isset($input['bio']) ? trim($input['bio']) : '';
    $subject_id = intval($input['subject_id']);
    
    // التحقق من صحة البيانات
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        throw new Exception("البريد الإلكتروني غير صحيح");
    }
    
    if (!preg_match('/^[0-9]{10}$/', $phone)) {
        throw new Exception("رقم الهاتف يجب أن يكون 10 أرقام");
    }
    
    if (!in_array($gender, ['male', 'female'])) {
        throw new Exception("الجنس غير صحيح");
    }
    
    // التحقق من وجود المادة
    $subject_check = $pdo->prepare("SELECT subject_id FROM subjects WHERE subject_id = ?");
    $subject_check->execute([$subject_id]);
    if (!$subject_check->fetch()) {
        throw new Exception("المادة المحددة غير موجودة");
    }
    
    // التحقق من عدم تكرار البريد الإلكتروني
    $email_check = $pdo->prepare("SELECT email FROM teachers WHERE email = ?");
    $email_check->execute([$email]);
    if ($email_check->fetch()) {
        throw new Exception("البريد الإلكتروني موجود مسبقاً");
    }
    
    // بدء المعاملة
    $pdo->beginTransaction();
    
    // إنشاء اسم المستخدم وكلمة المرور
    $username_base = strtolower(str_replace(' ', '', $name));
    $username = $username_base . rand(100, 999);
    $password = generateRandomPassword();
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);
    
    // إضافة المستخدم أولاً
    $user_sql = "INSERT INTO users (username, password, role) VALUES (?, ?, 'teacher')";
    $user_stmt = $pdo->prepare($user_sql);
    $user_stmt->execute([$username, $hashed_password]);
    $user_id = $pdo->lastInsertId();
    
    // إضافة المعلم
    $teacher_sql = "INSERT INTO teachers (name, email, phone, birth_date, gender, address, department, bio, subject_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    $teacher_stmt = $pdo->prepare($teacher_sql);
    $teacher_stmt->execute([$name, $email, $phone, $birth_date, $gender, $address, $department, $bio, $subject_id, $user_id]);
    
    // تأكيد المعاملة
    $pdo->commit();
    
    // إرسال الاستجابة
    echo json_encode([
        "success" => true,
        "message" => "تم إضافة المعلم بنجاح",
        "username" => $username,
        "password" => $password,
        "teacher_id" => $pdo->lastInsertId()
    ], JSON_UNESCAPED_UNICODE);
    
} catch (Exception $e) {
    // التراجع عن المعاملة في حالة الخطأ
    if ($pdo->inTransaction()) {
        $pdo->rollback();
    }
    
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}

function generateRandomPassword($length = 8) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $password = '';
    for ($i = 0; $i < $length; $i++) {
        $password .= $characters[rand(0, strlen($characters) - 1)];
    }
    return $password;
}

$pdo = null;
?>